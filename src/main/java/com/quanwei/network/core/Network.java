package com.quanwei.network.core;

/**
 * 网络配置类
 */
public class Network {

    /**
     * 网口名称
     */
    private String name;

    /**
     * 网络ip地址
     */
    private String ip;

    /**
     * 掩码
     */
    private String netmask;

    /**
     * 网关
     */
    private String gateway;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getNetmask() {
        return netmask;
    }

    public void setNetmask(String netmask) {
        this.netmask = netmask;
    }

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }
}
