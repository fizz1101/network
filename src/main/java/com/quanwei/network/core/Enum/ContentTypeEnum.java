package com.quanwei.network.core.Enum;

/**
 * 数据类型枚举类
 */
public enum ContentTypeEnum implements EnumCommon {
    JSON("1", "application/json"),
    XML("2", "text/xml"),
    TEXT("3", "text/plain"),
    URLENCODE("4", "application/x-www-form-urlencoded"),
    FORMDATA("5", "multipart/form-data"),
    SOAP("6", "application/soap+xml")
    ;

    private String type;
    private String contentType;

    ContentTypeEnum(String type, String contentType) {
        this.type = type;
        this.contentType = contentType;
    }

    @Override
    public String getKey() {
        return type;
    }

    @Override
    public String getValue() {
        return contentType;
    }
}
