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
    private String device;

    /**
     * 网口启用开关("YES":开;"NO":关)
     */
    private String onBoot;

    /**
     * 网络ip地址
     */
    @Pattern(regexp = RegexUtil.REGEX_IP, message = "ip地址格式错误")
    private String ipAddr;

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

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getOnBoot() {
        return onBoot;
    }

    public void setOnBoot(String onBoot) {
        this.onBoot = onBoot;
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
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
