package com.quanwei.network.core.entity;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("network")
@PropertySource(value = {"classpath:network.properties",
        "file:${spring.profiles.path}/network.properties"}, ignoreResourceNotFound = true)
public class NetworkConf {

    public static final String DEVICE = "DEVICE";
    public static final String IPADDR = "IPADDR";
    public static final String NETMASK = "NETMASK";
    public static final String GATEWAY = "GATEWAY";

    public static String path;

    public static String fileNameHeader;

    public static String wan1;

    public static String wan2;

    public static String wan3;

    public static String wan4;

    public static String wan5;

    public static String wan6;

    public void setPath(String path) {
        NetworkConf.path = path;
    }

    public void setFileNameHeader(String fileNameHeader) {
        NetworkConf.fileNameHeader = fileNameHeader;
    }

    public void setWan1(String wan1) {
        NetworkConf.wan1 = wan1;
    }

    public void setWan2(String wan2) {
        NetworkConf.wan2 = wan2;
    }

    public void setWan3(String wan3) {
        NetworkConf.wan3 = wan3;
    }

    public void setWan4(String wan4) {
        NetworkConf.wan4 = wan4;
    }

    public void setWan5(String wan5) {
        NetworkConf.wan5 = wan5;
    }

    public void setWan6(String wan6) {
        NetworkConf.wan6 = wan6;
    }
}
