package com.quanwei.network.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 网卡设置
 */
@RestController
@RequestMapping(value = "/network")
public class NetworkController {

    private Logger logger = LoggerFactory.getLogger(NetworkController.class);

    /**
     * 获取网卡信息列表
     * @return
     */
    @RequestMapping(value = "/get_network_list")
    public Map<String, Object> getNetworkList() throws Exception {
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
                //返回指定参数
                /*Map<String, Object> pageMap = new HashMap<>();
                pageMap.put(NetworkConf.DEVICE, contentMap.containsKey(NetworkConf.DEVICE) ? contentMap.get(NetworkConf.DEVICE) : "");
                pageMap.put(NetworkConf.IPADDR, contentMap.containsKey(NetworkConf.IPADDR) ? contentMap.get(NetworkConf.IPADDR) : "");
                pageMap.put(NetworkConf.NETMASK, contentMap.containsKey(NetworkConf.NETMASK) ? contentMap.get(NetworkConf.NETMASK) : "");
                pageMap.put(NetworkConf.GATEWAY, contentMap.containsKey(NetworkConf.GATEWAY) ? contentMap.get(NetworkConf.GATEWAY) : "");
                netInfoList.add(pageMap);*/
                netInfoList.add(contentMap);
            }
            type = 0;
            result = netInfoList;
        } catch (Exception e) {
            type = 2;
            logger.error(e.toString());
            e.printStackTrace();
        }
        return returnJsonStr(type, "获取网卡信息列表", result);
    }

    /**
     * 获取单个网卡信息
     * @param device
     * @return
     */
    @RequestMapping(value = "/get_network_info")
    public Map<String, Object> getNetworkInfo(String device) {
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
            //返回指定参数
            /*Map<String, Object> pageMap = new HashMap<>();
            pageMap.put(NetworkConf.DEVICE, contentMap.containsKey(NetworkConf.DEVICE) ? contentMap.get(NetworkConf.DEVICE) : "");
            pageMap.put(NetworkConf.IPADDR, contentMap.containsKey(NetworkConf.IPADDR) ? contentMap.get(NetworkConf.IPADDR) : "");
            pageMap.put(NetworkConf.NETMASK, contentMap.containsKey(NetworkConf.NETMASK) ? contentMap.get(NetworkConf.NETMASK) : "");
            pageMap.put(NetworkConf.GATEWAY, contentMap.containsKey(NetworkConf.GATEWAY) ? contentMap.get(NetworkConf.GATEWAY) : "");
            result = contentMap;*/
            type = 0;
            result = contentMap;
        } catch (Exception e) {
            type = 2;
            logger.error(e.toString());
            e.printStackTrace();
        }
        return returnJsonStr(type, "获取网卡详细信息", result);
    }

    /**
     * 保存网络设置
     * @param network
     * @return
     */
    @RequestMapping(value = "/save_network")
    public Map<String, Object> saveNetwork(Network network) {
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
            logger.error(e.toString());
            e.printStackTrace();
        }
        return returnJsonStr(type, "保存网络设置", result);
    }

    /**
     * 获取网卡类型列表
     * @return
     */
    @RequestMapping(value = "/get_network_type")
    public Map<String, Object> getNetworkType() {
        int type = 1;
        Object result = "";
        try {
            List<Map<String, Object>> typeList = new ArrayList<>();
            File folder = new File(NetworkConf.path);
            List<File> list_file = FileUtil.getFileListByName(folder.getPath(), NetworkConf.fileNameHeader, "-lo", 0);
            for (File file : list_file) {
                Map<String, Object> cell = new HashMap<>();
                String fileName = file.getName();
                fileName = fileName.replaceAll(NetworkConf.fileNameHeader, "");
                cell.put("key", fileName);
                cell.put("value", fileName);
                typeList.add(cell);
            }
            type = 0;
            result = typeList;
        } catch (Exception e) {
            type = 2;
            logger.error(e.toString());
            e.printStackTrace();
        }
        return returnJsonStr(type, "获取网卡类型列表", result);
    }

    /**
     * 获取路由配置
     * @return
     */
    @RequestMapping(value = "get_route_info")
    public Map<String, Object> getRouteInfo(@RequestParam(required = false) String device) {
        int type = 1;
        Object result = "";
        try {
            List<Network> resList = new ArrayList<>();
            //TODO 获取路由信息
            String routeListStr = ShellUtil.getRoute(device);
            String[] arr_route = routeListStr.split("\r\n");
            for (String route : arr_route) {
                String[] arr_column = route.split("\\s+");
                Network network = new Network();
                network.setIp(arr_column[0]);
                network.setGateway(arr_column[1]);
                network.setNetmask(arr_column[2]);
                network.setName(arr_column[6]);
                resList.add(network);
            }
            type = 0;
            result = resList;
        } catch (Exception e) {
            type = 2;
            logger.error(e.toString());
            e.printStackTrace();
        }
        return returnJsonStr(type, "获取路由配置", result);
    }

    /**
     * 保存路由配置
     * @return
     */
    @RequestMapping(value = "save_route")
    public Map<String, Object> saveRouteInfo(Network network, String opt) {
        int type = 1;
        Object result = "";
        try {
            //TODO 更新路由信息
            ShellUtil.updateRoute(network, opt);
            type = 0;
        } catch (Exception e) {
            type = 2;
            logger.error(e.toString());
            e.printStackTrace();
        }
        return returnJsonStr(type, "保存路由配置", result);
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
    public void test() {
        try {
            /*String filePath = NetworkConf.path + File.separator + NetworkConf.fileNameHeader + "ens32";
            ShellUtil.addToFile(filePath, "AAA=aaa", true);
            ShellUtil.addToFile(filePath, "BBB=bbb", true);*/
            String list_route = ShellUtil.getRoute("ens32");
            System.out.println("路由列表：" + list_route);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 返回结果体
     * @param type
     * @param msg
     * @param result
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
