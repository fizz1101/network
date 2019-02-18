package com.quanwei.network.core.Enum;

/**
 * shell命令执行返回码
 */
public enum ShellCodeEnum {

    SUCCESS(0, "success"),
    PERMIT(1, "Operation not permitted"),
    NOFILE(2, "No such file or directory"),
    PROCESS(3, "No such process"),
    FILELARGE(27, "File too large");

    private int code;
    private String msg;

    ShellCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
