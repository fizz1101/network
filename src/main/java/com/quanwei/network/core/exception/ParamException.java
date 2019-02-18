package com.quanwei.network.core.exception;


import com.quanwei.network.core.Enum.ErrorCodeEnum;

/**
 * 统一抛出参数异常
 */
public class ParamException extends RuntimeException {

    private String errCode;
    private String errMsg;

    public ParamException(ErrorCodeEnum errorCodeEnum) {
        this(errorCodeEnum.getKey(), errorCodeEnum.getValue());
    }

    public ParamException(String errCode, String errMsg) {
        super(errMsg);
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    public ParamException(Throwable cause) {
        super(cause);
    }

    public ParamException(String message) {
        super(message);
    }

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

}
