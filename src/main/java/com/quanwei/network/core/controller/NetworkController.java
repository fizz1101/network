package com.quanwei.network.core.controller;

import com.quanwei.network.core.Enum.ErrorCodeEnum;
import com.quanwei.network.core.entity.ResponseEntity;
import com.quanwei.network.core.util.ParamUtil;
import com.quanwei.network.core.util.ShellUtil;
import com.quanwei.network.core.entity.Network;
import com.quanwei.network.core.entity.NetworkConf;
import com.quanwei.network.core.util.FileUtil;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.*;

/**
 * 网卡设置
 *
 * restful接口统一返回格式：
 * {
 *     "code": 0,
 *     "msg": "",
 *     "result": {
 *
 *     }
 * }
 */
@RestController
@RequestMapping(value = "/network")
public class NetworkController extends BaseController {

//    private Logger logger = LoggerFactory.getLogger(NetworkController.class);

    private static final String ROUTEHEADERFILTER1 = "Kernel";
    private static final String ROUTEHEADERFILTER2 = "Destination";

    /**
     * 网口列表页面
     * @return
     */
    @RequestMapping(value = "/network_list")
    public ModelAndView networkPage() {
        return new ModelAndView("network.html");
    }

    /**
     * 路由列表页面
     * @return
     */
    public ModelAndView routePage() {
        return new ModelAndView("route.html");
    }

    /**
     * 获取网卡类型列表
     * @param response
     *    "result": [
     *         {
     *             "key": "ens32",
     *             "value": "ens32"
     *         }
     *     ]
     * @throws Exception
     */
    @RequestMapping(value = "/get_network_type")
    public void getNetworkType(HttpServletResponse response) throws Exception {
        List<Map<String, Object>> typeList = new ArrayList<>();
        List<String> list_file = ShellUtil.fileList(NetworkConf.path, NetworkConf.fileNameHeader, "lo|bak");
        for (String fileName : list_file) {
            Map<String, Object> cell = new HashMap<>();
            String device = fileName.replace(NetworkConf.fileNameHeader, "");
            cell.put("key", device);
            cell.put("value", device);
            typeList.add(cell);
        }

        /*File folder = new File(NetworkConf.path);
        List<File> list_file = FileUtil.getFileListByName(folder.getPath(), NetworkConf.fileNameHeader, "-lo", 0);
        for (File file : list_file) {
            Map<String, Object> cell = new HashMap<>();
            String fileName = file.getName();
            fileName = fileName.replaceAll(NetworkConf.fileNameHeader, "");
            cell.put("key", fileName);
            cell.put("value", fileName);
            typeList.add(cell);
        }*/
        ResponseEntity responseEntity = new ResponseEntity(ErrorCodeEnum.SUCCESS, typeList);
        renderJson(response, responseEntity);
    }

    /**
     * 获取网卡信息列表
     * @param response
     *    "result": {
     *         {
     *             "SPEED": "auto", //网口速率("auto"/10/100/1000)
     *             "NETMASK": "255.255.255.0", //子网掩码
     *             "GATEWAY": "192.168.1.1", //网关
     *             "RXPACKETS": " 1931284", //接收包
     *             "DEVICE": "ens32", //接口名称
     *             "ONBOOT": "yes", //是否启用("yes"/"no")
     *             "DUPLEX": "auto", //工作模式(Full/Half)
     *             "TXPACKETS": " 37411156", //发送包
     *             "IPADDR": "192.168.1.135" //IPV4地址
     *         }
     *     }
     * @throws Exception
     */
    @RequestMapping(value = "get_network_list")
    public void getNetworkList(HttpServletResponse response) throws Exception {
        Map<String, Object> resMap = new HashMap<>();
        List<Map<String, Object>> netInfoList = new ArrayList<>();
        //获取目录下文件列表
        List<String> list_file = ShellUtil.fileList(NetworkConf.path, NetworkConf.fileNameHeader, "lo|bak");
        for (String fileName : list_file) {
            String filePath = NetworkConf.path + File.separator + fileName;
            String device = fileName.replace(NetworkConf.fileNameHeader, "");
            Map<String, Object> infoMap = ShellUtil.readFile(filePath, NetworkConf.DEVICE, NetworkConf.ONBOOT,
                    NetworkConf.IPADDR, NetworkConf.NETMASK, NetworkConf.GATEWAY);
            //获取网卡速率与工作模式
            Map<String, Object> swMap = new HashMap<>();
            List<Map<String, Object>> swList = ShellUtil.getSpeedAndWork(device);
            if (swList.size() == 1) {
                swMap = swList.get(0);
            } else {
                swMap.put(NetworkConf.SPEED, "auto");
                swMap.put(NetworkConf.DUPLEX, "auto");
            }
            infoMap.putAll(swMap);
            //获取网口接收包
            Map<String, Object> packetMap = ShellUtil.getNetworkPacket(device);
            infoMap.putAll(packetMap);
            netInfoList.add(infoMap);
        }
        resMap.put("totle", netInfoList.size());
        resMap.put("row", netInfoList);
        ResponseEntity entity = new ResponseEntity(ErrorCodeEnum.SUCCESS, resMap);
        renderJson(response, entity);
    }

    /**
     * 获取网卡信息列表(备用)
     * @return
     */
    @RequestMapping(value = "/get_network_list_bak")
    public Map<String, Object> getNetworkListBak() {
        int type = 1;
        Object result = "";
        try {
            List<Map<String, Object>> netInfoList = new ArrayList<>();
            File folder = new File(NetworkConf.path);
            //获取文件列表
            List<File> list_file = FileUtil.getFileListByName(folder.getPath(), NetworkConf.fileNameHeader, "-lo", 0);
            for (File file : list_file) {
                Map<String, Object> contentMap = new HashMap<>();
                //读取文件内容
                List<List<String>> list_content = FileUtil.readFile(file.getPath(), "=", "utf-8");
                for (List<String> list_line : list_content) {
                    String value = list_line.size() > 1 ? list_line.get(1) : "";
                    contentMap.put(list_line.get(0), value);
                }
                netInfoList.add(contentMap);
            }
            type = 0;
            result = netInfoList;
        } catch (Exception e) {
            type = 2;
            logger.error(e.toString(), e.getCause());
        }
        return returnJsonStr(type, "获取网卡信息列表", result);
    }

    /**
     * 获取单网卡信息详情
     * @param request
     *  device 网口名称(必传)
     * @param response
     *    "result": {
     *         "SPEED": "auto", //网口速率("auto"/10/100/1000)
     *         "NETMASK": "255.255.255.0", //子网掩码
     *         "GATEWAY": "192.168.1.1", //网关
     *         "RXPACKETS": "38206386", //接收包
     *         "DEVICE": "ens32", //接口名称
     *         "ONBOOT": "yes", //是否启用("yes"/"no")
     *         "TXPACKETS": "1953070", //发送包
     *         "DUPLEX": "auto", //工作模式(Full/Half)
     *         "IPADDR": "192.168.1.135" //IPV4地址
     *     }
     * @throws Exception
     */
    @RequestMapping(value = "get_network_info")
    public void getNetworkInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> paramMap = ParamUtil.getMapData(request);
        String device = ParamUtil.getRequiredString(paramMap, "device");
        String filePath = NetworkConf.path + File.separator + NetworkConf.fileNameHeader + device;
        Map<String, Object> infoMap = ShellUtil.readFile(filePath, NetworkConf.DEVICE, NetworkConf.ONBOOT,
                NetworkConf.IPADDR, NetworkConf.NETMASK, NetworkConf.GATEWAY);
        //获取网口速率与工作模式
        Map<String, Object> swMap = new HashMap<>();
        List<Map<String, Object>> swList = ShellUtil.getSpeedAndWork(device);
        if (swList.size() == 1) {
            swMap = swList.get(0);
        } else {
            swMap.put(NetworkConf.SPEED, "auto");
            swMap.put(NetworkConf.DUPLEX, "auto");
        }
        infoMap.putAll(swMap);
        //获取网口接收包
        Map<String, Object> packetMap = ShellUtil.getNetworkPacket(device);
        infoMap.putAll(packetMap);
        ResponseEntity responseEntity = new ResponseEntity(ErrorCodeEnum.SUCCESS, infoMap);
        renderJson(response, responseEntity);
    }

    /**
     * 获取单个网卡信息(备用)
     * @param device 网卡名称
     * @return
     */
    @RequestMapping(value = "/get_network_info_bak")
    public Map<String, Object> getNetworkInfoBak(String device) {
        int type = 1;
        Object result = "";
        try {
            String filePath = NetworkConf.path + File.separator + NetworkConf.fileNameHeader + device;
            Map<String, Object> contentMap = new HashMap<>();
            //读取文件内容
            List<List<String>> list_content = FileUtil.readFile(filePath, "=", "utf-8");
            for (List<String> list_line : list_content) {
                String value = list_line.size() > 1 ? list_line.get(1) : "";
                contentMap.put(list_line.get(0), value);
            }
            type = 0;
            result = contentMap;
        } catch (Exception e) {
            type = 2;
            logger.error(e.toString(), e.getCause());
        }
        return returnJsonStr(type, "获取网卡详细信息", result);
    }

    /**
     * 保存网络设置
     * @param network 网卡实体类
     * @param request
     *  speed: 速率(10/100/1000)
     *  work: 工作模式(Full/Half)
     * @param response
     */
    @RequestMapping(value = "save_network")
    public void saveNetwork(@Validated Network network, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> paramMap = ParamUtil.getMapData(request);
        String device = ParamUtil.getRequiredString(paramMap, "device");
        String filePath = NetworkConf.path + File.separator + NetworkConf.fileNameHeader + device;
        //更新网络配置
        Map<String, Object> oldNetworkMap = ShellUtil.readFile(filePath, NetworkConf.DEVICE, NetworkConf.ONBOOT,
                NetworkConf.IPADDR, NetworkConf.NETMASK, NetworkConf.GATEWAY);
        //修改ip
        if (oldNetworkMap.containsKey(NetworkConf.IPADDR)) {
            ShellUtil.updateNetwork(filePath, NetworkConf.IPADDR, network.getIp());
        } else {
            String content = NetworkConf.IPADDR + "=" + network.getIp();
            ShellUtil.addToFile(filePath, content, true);
        }
        //修改掩码
        if (oldNetworkMap.containsKey(NetworkConf.NETMASK)) {
            ShellUtil.updateNetwork(filePath, NetworkConf.NETMASK, network.getNetmask());
        } else {
            String content = NetworkConf.NETMASK + "=" + network.getNetmask();
            ShellUtil.addToFile(filePath, content, true);
        }
        //修改网关
        if (oldNetworkMap.containsKey(NetworkConf.GATEWAY)) {
            ShellUtil.updateNetwork(filePath, NetworkConf.GATEWAY, network.getGateway());
        } else {
            String content = NetworkConf.GATEWAY + "=" + network.getGateway();
            ShellUtil.addToFile(filePath, content, true);
        }
        //修改开关
        if (oldNetworkMap.containsKey(NetworkConf.ONBOOT)) {
            ShellUtil.updateNetwork(filePath, NetworkConf.ONBOOT, network.getBoot());
        } else {
            String content = NetworkConf.ONBOOT + "=" + network.getBoot();
            ShellUtil.addToFile(filePath, content, true);
        }
        //修改速率与工作模式
        ShellUtil.setSpeedAndWork(device, ParamUtil.getRequiredInteger(paramMap, "speed"), ParamUtil.getRequiredString(paramMap, "work"));

        /*Map<String, Object> oldNetworkMap = ShellUtil.readFile(filePath, NetworkConf.DEVICE, NetworkConf.ONBOOT,
                NetworkConf.IPADDR, NetworkConf.NETMASK, NetworkConf.GATEWAY);
        for (String key : oldNetworkMap.keySet()) {
            ShellUtil.updateNetwork(filePath, key, ParamUtil.getRequiredString(paramMap, key));
            paramMap.remove(key);
        }
        for (String key : paramMap.keySet()) {
            String content = key + "=" + ParamUtil.getRequiredString(paramMap, key);
            ShellUtil.addToFile(filePath, content, true);
        }*/

        //重启网卡
        ShellUtil.restartNetwork();
        ResponseEntity responseEntity = new ResponseEntity(ErrorCodeEnum.SUCCESS, "");
        renderJson(response, responseEntity);
    }

    /**
     * 保存网络设置(备用)
     * @param network
     * @return
     */
    @RequestMapping(value = "/save_network_bak")
    public Map<String, Object> saveNetworkBak(@Validated @RequestBody Network network) {
        int type = 1;
        Object result = "";
        try {
            String device = network.getName();
            if (!StringUtils.isEmpty(device)) {
                Map<String, String> networkInfoMap = new HashMap<>();
                Map<String, String> replaceMap = new HashMap<>();
                String addContent = "";
                networkInfoMap.put(NetworkConf.DEVICE, device);
                String filePath = NetworkConf.path + File.separator + NetworkConf.fileNameHeader + device;
                //读取原文件内容
                List<List<String>> list_content = FileUtil.readFile(filePath, "=", "utf-8");
                List<String> key_list = new ArrayList<>();
                for (List<String> list_line : list_content) {
                    key_list.add(list_line.get(0));
                }
                String ip = network.getIp();
                if (!StringUtils.isEmpty(ip)) {
                    if (key_list.contains(NetworkConf.IPADDR)) {
                        String oldStrRegex = "(?<=((^|\\s+)" + NetworkConf.IPADDR + "=))(.+?)(?=($|\\s+))";
                        replaceMap.put(oldStrRegex, ip);
                    } else {
                        String content = NetworkConf.IPADDR+"="+ip;
                        ShellUtil.addToFile(filePath, content, true);
                        addContent += content;
                    }
                    networkInfoMap.put(NetworkConf.IPADDR, ip);
//                    ShellUtil.updateNetwork(name, NetworkConf.IPADDR, ip);
                }
                String netmask = network.getNetmask();
                if (!StringUtils.isEmpty(netmask)) {
                    if (key_list.contains(NetworkConf.NETMASK)) {
                        String oldStrRegex = "(?<=((^|\\s+)" + NetworkConf.NETMASK + "=))(.+?)(?=($|\\s+))";
                        replaceMap.put(oldStrRegex, netmask);
                    } else {
                        String content = NetworkConf.NETMASK+"="+netmask;
                        ShellUtil.addToFile(filePath, content, true);
                        addContent += content;
                    }
                    networkInfoMap.put(NetworkConf.NETMASK, netmask);
//                    ShellUtil.updateNetwork(name, NetworkConf.NETMASK, netmask);
                }
                String gateway = network.getGateway();
                if (!StringUtils.isEmpty(gateway)) {
                    if (key_list.contains(NetworkConf.GATEWAY)) {
                        String oldStrRegex = "(?<=((^|\\n)" + NetworkConf.GATEWAY + "=))(.+?)(?=($|\\n))";
                        replaceMap.put(oldStrRegex, gateway);
                    } else {
                        String content = NetworkConf.GATEWAY+"="+gateway;
                        ShellUtil.addToFile(filePath, content, true);
                        addContent += content;
                    }
                    networkInfoMap.put(NetworkConf.GATEWAY, gateway);
//                    ShellUtil.updateNetwork(name, NetworkConf.GATEWAY, gateway);
                }
                FileUtil.replaceContent(filePath, replaceMap);
                //重启网卡
                ShellUtil.restartNetwork();
                type = 0;
                result = networkInfoMap;
            }
        } catch (Exception e) {
            type = 2;
            logger.error(e.toString(), e.getCause());
        }
        return returnJsonStr(type, "保存网络设置", result);
    }

    /**
     * 启用/禁用网口
     * @param request
     *  device：网口名称(必传)
     *  status：操作状态(1:启用;0:禁用)(必传)
     * @param response
     *  "result": ""
     * @throws Exception
     */
    @RequestMapping(value = "update_network_status")
    public void updateNetworkStatus(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> paramMap = ParamUtil.getMapData(request);
        String device = ParamUtil.getRequiredString(paramMap, "device");
        int status = ParamUtil.getRequiredInteger(paramMap, "status");
        /*String opt = "up";
        if (status == 0) {
            opt = "down";
        }
        ShellUtil.networkUpOrDown(device, opt);*/
        String opt = "yes";
        if (status == 0) {
            opt = "no";
        }
        ShellUtil.updateNetwork(device, NetworkConf.ONBOOT, opt);
        ShellUtil.restartNetwork();
        ResponseEntity responseEntity = new ResponseEntity(ErrorCodeEnum.SUCCESS, "");
        renderJson(response, responseEntity);
    }

    /**
     * 获取路由配置
     * @param request
     *  device: 网口名称
     * @param response
     *     "result": [
     *         {
     *             "dev": "ens32", //网口名称
     *             "ip": "192.168.1.135", //目的地址
     *             "via": "192.168.1.1", //关联网关
     *             "proto": "static", //静态路由
     *             "scope": link",
     *             "metric": 100
     *         }
     *     ]
     * @throws Exception
     */
    @RequestMapping(value = "get_route_list")
    public void getRouteList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> paramMap = ParamUtil.getMapData(request);
        String device = ParamUtil.getString(paramMap, "device");
        Map<String, Object> resMap = new HashMap<>();
        List<Object> resList = new ArrayList<>();
        //获取路由信息
        String routeListStr = ShellUtil.getRoute(device);
        String[] arr_route = routeListStr.split("\r\n");
        for (String route : arr_route) {
            if (!route.contains(ROUTEHEADERFILTER1) && !route.contains(ROUTEHEADERFILTER2)) {
                String[] arr_column = route.split("\\s+");
                Map<String, String> routeInfo = new HashMap<>();
                routeInfo.put("ip", arr_column[0]);
                for (int i=1; i<arr_column.length; i++) {
                    routeInfo.put(arr_column[i], arr_column[++i]);
                }
                /*Network network = new Network();
                network.setIp(arr_column[0]);
                network.setGateway(arr_column[1]);
                network.setNetmask(arr_column[2]);
                network.setName(arr_column[7]);*/
                resList.add(routeInfo);
            }
        }
        resMap.put("total", resList.size());
        resMap.put("row", resList);
        ResponseEntity responseEntity = new ResponseEntity(ErrorCodeEnum.SUCCESS, resMap);
        renderJson(response, responseEntity);
    }

    /**
     * 保存路由配置
     * @param request
     *  opt: 操作类型("add"/"del")(必传)
     * @param response
     * @param network 网卡实体类
     * @throws Exception
     */
    @RequestMapping(value = "save_route")
    public void saveRouteInfo(HttpServletRequest request, HttpServletResponse response,
                                             Network network) throws Exception {
        Map<String, Object> paramMap = ParamUtil.getMapData(request);
        String opt = ParamUtil.getRequiredString(paramMap, "opt");
        //更新路由信息
        ShellUtil.updateRoute(network, opt);
        ResponseEntity responseEntity = new ResponseEntity(ErrorCodeEnum.SUCCESS, "");
        renderJson(response, responseEntity);
    }

    /**
     * 重启网卡(仅供测试)
     */
    @RequestMapping(value = "restart_network")
    public void restartNetwork() {
        try {
            ShellUtil.restartNetwork();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试
     */
    @RequestMapping(value = "test")
    public void test(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> paramMap = ParamUtil.getMapData(request);
        String device = ParamUtil.getRequiredString(paramMap, "device");
        //添加内容测试
        /*String filePath = NetworkConf.path + File.separator + NetworkConf.fileNameHeader + "ens32";
        ShellUtil.addToFile(filePath, "AAA=aaa", true);
        ShellUtil.addToFile(filePath, "BBB=bbb", true);*/
        //获取路由列表测试
        /*String list_route = ShellUtil.getRoute("ens32");
        System.out.println("路由列表：" + list_route);*/
        //修改网络配置
        /*ShellUtil.updateNetwork(device, NetworkConf.NETMASK, "0.0.0.0");*/
        //获取网口接收包
        //Map<String, Object> packetMap = ShellUtil.getNetworkPacket(device);
        //获取网口速率与工作模式
        /*List<Map<String, Object>> modeList = ShellUtil.getSpeedAndWork(device);
        ResponseEntity responseEntity = new ResponseEntity(ErrorCodeEnum.SUCCESS, modeList);
        renderJson(response, responseEntity);*/
        //启用禁用网口
    }

    /**
     * 返回结果体
     * @param type 结果类型
     * @param msg 结果说明
     * @param result 详情
     * @return
     */
    private Map<String, Object> returnJsonStr(int type, String msg, Object result) {
        Map<String, Object> resMap = new HashMap<>();
        switch (type) {
            case 0:
                resMap.put("code", 0);
                resMap.put("msg", msg+"成功");
                break;
            case 1:
                resMap.put("code", 1);
                resMap.put("msg", msg+"失败");
                break;
            case 2:
                resMap.put("code", 999);
                resMap.put(msg, msg+",系统异常");
                break;
            default:
                break;
        }
        resMap.put("result", result);
        return resMap;
    }

    /*@ExceptionHandler()
    public Map<String, Object> bindException(Exception ex) {
        ex.printStackTrace();
        logger.error(ex.toString());
        return returnJsonStr(2, "", "");
    }*/

}
