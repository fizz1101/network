package com.quanwei.network.core.Enum;

/**
 * 返回结果枚举类
 */
public enum ErrorCodeEnum implements EnumCommon {
    SUCCESS("0", "操作执行成功"),
    REQUEST_SUCCESS("200", "请求成功"),
    PARAM_ERROR("40000", "参数异常"),
    PARAM_TYPE_ERROR("40001", "参数类型异常"),
    PARAM_EMPTY("40002", "参数不存在或为空"),
    ERROR("50000", "系统异常")
    ;

    private String code;
    private String msg;

    ErrorCodeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public String getKey() {
        return code;
    }

    @Override
    public String getValue() {
        return msg;
    }
}
