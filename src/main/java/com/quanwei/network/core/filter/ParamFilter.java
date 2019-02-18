package com.quanwei.network.core.filter;

import com.quanwei.network.core.Enum.ContentTypeEnum;
import com.quanwei.network.core.entity.constant.ContentTypeConst;
import com.quanwei.network.core.util.JacksonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

/**
 * 请求参数过滤器
 */
@WebFilter(urlPatterns = "/*", initParams = {
        @WebInitParam(name="exclusions", value = ".html,.js,.css,.gif,.jpg,.png,.bmp,.ico") //忽略静态资源
})
public class ParamFilter implements Filter {

    private static Logger logger = LoggerFactory.getLogger(ParamFilter.class);

    //过滤参数数组
    private List<String> exclusionList = new ArrayList<>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //获取初始化过滤参数
        String exclusions = filterConfig.getInitParameter("exclusions");
        if (!StringUtils.isEmpty(exclusions)) {
            String[] arr_excu = exclusions.split(",");
            exclusionList = Arrays.asList(arr_excu);
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        //请求url
        String url = httpServletRequest.getRequestURL().toString();
        //请求参数类型
        String contentType = httpServletRequest.getContentType();
        if (!isFilterUrl(url)) {
            /*if (StringUtils.isEmpty(contentType)) {
                if (logger.isWarnEnabled()) {
                    if (!httpServletRequest.getMethod().equals("HEAD")) {
                        logger.warn("请求没有带requestType!,URL={}", url);
                    }
                }
            } else {*/
                if (contentType != null && contentType.indexOf(";") > 0) {
                    contentType = contentType.substring(0, contentType.indexOf(";"));
                }
                //请求内容长度
                int contentLength = servletRequest.getContentLength();
                logger.info("请求内容长度：" + contentLength);
                if (contentType == null || contentLength > 0) {
                    if (contentType == null) {
                        contentType = "";
                    }
                    Map<String, Object> dataMap = new HashMap<>();
                    MyRequestWrapper myRequest = new MyRequestWrapper(httpServletRequest);
                    Map<String, String[]> requestMap = myRequest.getParameterMap();
                    switch (contentType) {
                        case ContentTypeConst.JSON:
                            //解析json格式数据
                            String reqBody = myRequest.getBody();
                            dataMap = JacksonUtil.buildNormalBinder().toObject(reqBody, Map.class);
                            break;
                        case ContentTypeConst.FORMDATA:
                            //解析form-data格式数据
                            for (Map.Entry<String, String[]> entry : requestMap.entrySet()) {
                                dataMap.put(entry.getKey(), entry.getValue()[0]);
                            }
                            break;
                        case ContentTypeConst.URLENCODE:
                            //解析x-www-form-urlencoded格式数据
                            for (Map.Entry<String, String[]> entry : requestMap.entrySet()) {
                                dataMap.put(entry.getKey(), entry.getValue()[0]);
                            }
                            break;
                        default:
                            //解析其他格式数据
                            for (Map.Entry<String, String[]> entry : requestMap.entrySet()) {
                                dataMap.put(entry.getKey(), entry.getValue()[0]);
                            }
                            break;
                    }
                    logger.info("IP："+httpServletRequest.getRemoteAddr()+"；URL："+url+"；PARAM："+dataMap);
                    myRequest.getSession().setAttribute("dataMap", dataMap);
                    filterChain.doFilter(myRequest, servletResponse);
                    return;
                }
//            }
        }
        filterChain.doFilter(servletRequest,servletResponse);
    }

    @Override
    public void destroy() {

    }

    /**
     * 判断是否过滤
     * @param url   请求url
     * @return
     */
    private boolean isFilterUrl(String url) {
        boolean flag = false;
        if (StringUtils.isEmpty(url)) {
            logger.error("请求url为空");
            throw new NullPointerException("校验请求url为空");
        } else {
            for (String key : exclusionList) {
                if (url.contains(key)) {
                    flag = true;
                    break;
                }
            }
        }
        return flag;
    }

}
