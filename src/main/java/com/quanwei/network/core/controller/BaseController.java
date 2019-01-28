package com.quanwei.network.core.controller;

import com.quanwei.network.core.Enum.ContentTypeEnum;
import com.quanwei.network.core.util.JacksonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 控制器基类
 */
public abstract class BaseController {

//    private static Logger logger = LoggerFactory.getLogger("operation");

    /**
     * 返回json数据
     * @param response
     * @param content   返回内容
     * @throws IOException
     */
    protected void renderJson(HttpServletResponse response, Object content) {
        renderContent(response, content, ContentTypeEnum.JSON.getValue(), "utf-8");
    }

    /**
     * 返回text数据
     * @param response
     * @param content   返回内容
     * @throws IOException
     */
    protected void renderText(HttpServletResponse response, Object content) {
        renderContent(response, content, ContentTypeEnum.TEXT.getValue(), "utf-8");
    }

    /**
     * 客户端返回数据
     * @param response
     * @param content
     * @param contentType
     * @param charset
     * @throws IOException
     */
    protected void renderContent(HttpServletResponse response, Object content, String contentType, String charset) {
        try {
            /*if (logger.isDebugEnabled()) {
                logger.debug(content.toString());
            }*/
            response.reset();
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setContentType(contentType);
            response.setCharacterEncoding(charset);
            if (ContentTypeEnum.JSON.getValue().equals(contentType)) {
                content = JacksonUtil.buildNonNullBinder().toJson(content);
            }
            response.getWriter().print(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
