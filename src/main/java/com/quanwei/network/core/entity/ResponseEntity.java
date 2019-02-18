package com.quanwei.network.core.entity;


import com.quanwei.network.core.Enum.ErrorCodeEnum;

import java.util.HashMap;

/**
 * 响应实体
 */
public class ResponseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 无返回值
     * @param errorsMsg
     */
    public ResponseEntity(ErrorCodeEnum errorsMsg){
        this(errorsMsg, null);
    }

    /**
     * 有返回值
     * @param errorsMsg
     * @param result
     */
    public ResponseEntity(ErrorCodeEnum errorsMsg, Object result){
        this(errorsMsg.getKey(), errorsMsg.getValue(), result);
    }

    /**
     * 自定义 返回码 和返回信息
     * @param errCode
     * @param errMsg
     * @param result
     */
    public ResponseEntity(String errCode, String errMsg, Object result){
        this.code = errCode;
        this.msg = errMsg;
        if(result == null)
            this.result = new HashMap<String,String>();
        else
            this.result = result;
    }

    /**
     * 响应码
     */
    private String code;

    /**
     * 响应码说明
     */
    private String msg;

    /**
     * 返回结果, 需能正确转化成json串
     */
    private Object result;


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    /*@Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }*/
}
