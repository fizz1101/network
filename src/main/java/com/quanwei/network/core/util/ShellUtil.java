package com.quanwei.network.core.util;

import com.quanwei.network.core.entity.Network;
import com.quanwei.network.core.entity.NetworkConf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.*;

public class ShellUtil {

    private static Logger logger = LoggerFactory.getLogger(ShellUtil.class);

    /**
     * 获取目录下文件
     * @param filePath 路径
     * @param include 包含参数
     * @param exclude 排除参数
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public static List<String> fileList(String filePath, String include, String exclude) throws IOException, InterruptedException {
        List<String> list_file = new ArrayList<>();
        String cmd = "ls " + filePath;
        if (!StringUtils.isEmpty(include)) {
            cmd += " | grep -E '" + include + "'";
        }
        if (!StringUtils.isEmpty(exclude)) {
            cmd += " | grep -vE '" + exclude + "'";
        }
        String fileListStr = exec(cmd);
        if (!StringUtils.isEmpty(fileListStr)) {
            list_file = Arrays.asList(fileListStr.split("\r\n"));
        }
        return list_file;
    }

    /**
     * 读取文件内容
     * @param filePath 文件路径
     * @param columns 所需字段(不传即整个文件)
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public static Map<String, Object> readFile(String filePath, String... columns) throws IOException, InterruptedException {
        Map<String, Object> resMap = new HashMap<>();
        String content = "";
        if (StringUtils.isEmpty(columns)) {
            //TODO 读取整个文件
        } else {
            //读取所需内容
            String columnStr = "";
            for (String column : columns) {
                columnStr += column + "|";
            }
            columnStr = columnStr.substring(0, columnStr.length()-1);
            logger.info("获取字段：" + columnStr);
            content = readContent(filePath, columnStr);
        }
        String[] arr_conline = content.replace("\"", "").split("\r\n");
        for (String line : arr_conline) {
            String[] arr_column = line.split("=");
            resMap.put(arr_column[0], arr_column[1]);
        }
        return resMap;
    }

    /**
     * 读取所需内容
     * @param filePath 文件路径
     * @param grep 过滤内容
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public static String readContent(String filePath, String grep) throws IOException, InterruptedException {
        String cmd = "grep -E '" + grep + "' " + filePath;
        return exec(cmd);
    }

    /**
     * 添加内容到文件
     * @param filePath 文件路径
     * @param content 内容
     * @param append 是否追加到文件末尾
     * @throws IOException
     * @throws InterruptedException
     */
    public static void addToFile(String filePath, String content, boolean append) throws IOException, InterruptedException {
        String cmd;
        if (append) {
            cmd = "echo '" + content + "' >> " + filePath;
        } else {
            cmd = "echo '" + content + "' > " + filePath;
        }
        exec(cmd);
    }

    /**
     * 修改网络配置
     * @param fileName 文件名
     * @param column 字段
     * @param value 值
     */
    public static void updateNetwork(String fileName, String column, String value) throws IOException, InterruptedException {
        String filePath = NetworkConf.path + File.separator + NetworkConf.fileNameHeader + fileName;
        String oldStr = "^" + column + "=(.+)$";
        String newStr = column + "=" + value;
        replace(filePath, oldStr, newStr);
    }

    /**
     * 替换内容
     * @param oldStr 旧字符串
     * @param newStr 新字符串
     * @param filePath 文件路径
     * @throws IOException
     * @throws InterruptedException
     */
    public static void replace(String filePath, String oldStr, String newStr) throws IOException, InterruptedException {
        String cmd = "sed -ri 's/" + oldStr + "/" + newStr + "/g' " + filePath;
        exec(cmd);
    }

    /**
     * 启用/禁用网口(临时)
     * @param device 网卡名称
     * @param opt 操作类型(up:启用;down:禁用)
     * @throws IOException
     * @throws InterruptedException
     */
    public static void networkUpOrDown(String device, String opt) throws IOException, InterruptedException {
        //centos7
        String cmd = "ip -l set " + device + " " + opt;
        //centos6
        //cmd = "ifconfig " + device + " " + opt;
        exec(cmd);
    }

    /**
     * 获取网卡运行状态信息
     * @param device 网卡名称
     * @param columns 所需字段(不传即所有内容)
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public static Map<String, Object> getNetMessage(String device, String... columns) throws IOException, InterruptedException {
        Map<String, Object> netMsgMap = new HashMap<>();
        String cmd = "ethtool " + device;
        if (!StringUtils.isEmpty(columns)) {
            String grep = "";
            for (String column : columns) {
                grep += column + "|";
            }
            grep = grep.substring(0, grep.length()-1);
            cmd += " | grep -iE '" + grep + "'";
        }
        String content = exec(cmd);
        String[] arr_conline = content.split("\r\n");
        for (String line : arr_conline) {
            String[] arr_column = line.replaceAll("\\s+", "").split(":");
            netMsgMap.put(arr_column[0].toUpperCase(), arr_column[1]);
        }
        return netMsgMap;
    }

    /**
     * 设置网口速率与工作模式
     * @param device 网卡名称
     * @param speed 速率
     * @param work 工作模式(Full:全双工;Half:半双工)
     * @throws IOException
     * @throws InterruptedException
     */
    public static void setSpeedAndWork(String device, String speed, String work) throws IOException, InterruptedException {
        String cmd = "ethtool -s " + device + " speed " + speed + " duplex " + work;
        exec(cmd);
    }

    /**
     * 获取网卡速率与工作模式
     * @param device 网卡名称
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public static List<Map<String, Object>> getSpeedAndWork(String device) throws IOException, InterruptedException {
        List<Map<String, Object>> modeList = new ArrayList<>();
        String mode = "Advertised link modes:";
        String cmd = "ethtool " + device + " | grep '" + mode + "'";
        String content = exec(cmd);
        content = content.replace(mode, "");
        String[] arr_cell = content.split("\\s+");
        for (String cell : arr_cell) {
            if (!StringUtils.isEmpty(cell)) {
                Map<String, Object> modeMap = new HashMap<>();
                String[] arr_column = cell.split(File.separator);
                modeMap.put(NetworkConf.SPEED, arr_column[0].replace("baseT", ""));
                modeMap.put(NetworkConf.DUPLEX, arr_column[1]);
                modeList.add(modeMap);
            }
        }
        return modeList;
    }

    /**
     * 获取网口收发包情况
     * @param device 网卡名称
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public static Map<String, Object> getNetworkPacket(String device) throws IOException, InterruptedException {
        Map<String, Object> resMap = new HashMap<>();
        String packet = "rx_packets|tx_packets";
        String cmd = "ethtool -S " + device + " | grep -E '" + packet + "'";
        String content = exec(cmd);
        String[] arr_conline = content.split("\r\n");
        for (String line : arr_conline) {
            String[] arr_column = line.replaceAll("\\s+", "").split(":");
            resMap.put(arr_column[0].replace("_", "").toUpperCase(), arr_column[1]);
        }
        return resMap;
    }

    /**
     * 获取网卡路由列表
     * @param device 网卡名称
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public static String getRoute(String device) throws IOException, InterruptedException {
        String cmd = "ip route show";
        if (!StringUtils.isEmpty(device)) {
            cmd += " | grep '" + device + "'";
        }
        return exec(cmd);
    }

    /**
     * 设置路由
     * @param network 网卡信息实体类
     * @param opt 操作类型(add:添加;del:删除)
     * @throws IOException
     * @throws InterruptedException
     */
    public static void updateRoute(Network network, String opt) throws IOException, InterruptedException {
        //centos7
        String cmd = "ip route " + opt + " " + network.getIpAddr() + " dev " + network.getDevice() + " proto static metric 100";
        if(!StringUtils.isEmpty(network.getGateway())) {
            cmd += " via " + network.getGateway();
        }
        //centos6
        //cmd = "route " + opt + " -net " + network.getIp() + " netmask " + network.getNetmask() + " " + network.getName();
        exec(cmd);
    }

    /**
     * 重启网卡
     * @throws IOException
     * @throws InterruptedException
     */
    public static void restartNetwork() throws IOException, InterruptedException {
        String cmd = "/etc/init.d/network restart";
        //cmd = "systemctl restart network";
        exec(cmd);
    }

    /**
     * 执行shell命令并返回结果
     * @param command
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    private static String exec(String command) throws IOException, InterruptedException {
        logger.info("执行shell命令：" + command);
        String[] cmd = {"/bin/bash", "-c", command};
        Runtime rt = Runtime.getRuntime();
        Process p = rt.exec(cmd);
        int res = p.waitFor();
        logger.info("Shell执行状态：" + res);
        InputStream in = p.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuffer sb = new StringBuffer();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
            sb.append("\r\n");
        }
        logger.info("Shell执行结果" + sb.toString());
        return sb.toString();
    }

    /**
     * 执行shell命令
     * @param command
     * @throws IOException
     * @throws InterruptedException
     */
    private static void exec_bak(String command) throws IOException, InterruptedException {
        logger.info("执行shell命令：" + command);
        //解决脚本没有执行权限
        /*ProcessBuilder builder = new ProcessBuilder(command);
        Process process = builder.start();
        int res = process.waitFor();
        logger.info("Shell执行状态1：" + res);*/

        String[] cmd = {"/bin/bash", "-c", command};
        Runtime rt = Runtime.getRuntime();
        Process p = rt.exec(cmd);
        int count = p.waitFor();
        logger.info("Shell执行状态：" + count);
        StringBuffer sb = new StringBuffer();
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        while((line=reader.readLine()) != null) {
            sb.append(line);
        }
        logger.info("Shell执行结果" + sb.toString());
    }

}
