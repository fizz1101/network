package com.quanwei.network.core.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则校验工具类
 */
public class RegexUtil {

    public static final String REGEX_IP = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])(\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)){3}$";

    /**
     * 校验ip地址
     * @param str_ip 校验字符串
     * @return
     */
    public static boolean checkIp(String str_ip) {
        return regex(str_ip, REGEX_IP);
    }

    /**
     * 正则校验
     * @param str 校验字符串
     * @param regex 校验规则
     * @return true/false
     */
    public static boolean regex(String str, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    public static void main(String[] args) {
        String ip = "1.168.100.255";
        System.out.println(checkIp(ip));
    }

}
