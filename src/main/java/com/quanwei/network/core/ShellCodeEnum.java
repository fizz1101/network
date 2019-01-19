package com.quanwei.network.core;

/**
 * shell命令执行返回码
 */
public enum ShellCodeEnum {

    SUCCESS(0, "success"),
    PERMIT(1, "Operation not permitted"),
    PROCESS(3, "No such process");

    private int code;
    private String msg;

    ShellCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
