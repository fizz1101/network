package com.quanwei.network.core.entity;

import com.quanwei.network.core.util.RegexUtil;

import javax.validation.constraints.Pattern;

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
    @Pattern(regexp = RegexUtil.REGEX_IP, message = "ip地址格式错误")
    private String ip;

    /**
     * 子网掩码
     */
    @Pattern(regexp = RegexUtil.REGEX_IP, message = "子网掩码格式错误")
    private String netmask;

    /**
     * 网关
     */
    @Pattern(regexp = RegexUtil.REGEX_IP, message = "网关格式错误")
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
