package com.quanwei.network.core.handler;

import com.quanwei.network.core.Enum.ErrorCodeEnum;
import com.quanwei.network.core.common.entity.ResponseEntity;
import com.quanwei.network.core.exception.ParamException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 全局异常处理类
 */
@ControllerAdvice
public class CustomExceptionHandler {

    private static Logger logger = LoggerFactory.getLogger(CustomExceptionHandler.class);

    /**
     * 统一处理抛出的参数异常
     * @param ex
     * @return
     */
    @ResponseBody
    @ExceptionHandler(ParamException.class)
    public ResponseEntity paramExceptionHandler(Exception ex) {
        ResponseEntity responseEntity = new ResponseEntity(ErrorCodeEnum.PARAM_ERROR);
        ParamException exception = (ParamException) ex;
        if (!StringUtils.isEmpty(exception.getErrCode())){
            logger.info("ParamException：" + exception.getErrMsg(), ex);
            responseEntity = new ResponseEntity(exception.getErrCode(),exception.getErrMsg(),null);
        }else{
            logger.error(ex.getMessage() ,ex);
        }
        return responseEntity;
    }

    /**
     * 统一处理抛出的默认异常
     * @param ex
     * @return
     */
    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ResponseEntity exceptionHandler(Exception ex) {
        logger.error(ex.getMessage() ,ex);
        ResponseEntity responseEntity = new ResponseEntity(ErrorCodeEnum.ERROR);
        return responseEntity;
    }

}
