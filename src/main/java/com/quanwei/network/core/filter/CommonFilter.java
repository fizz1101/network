package com.quanwei.network.core.filter;

import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@WebFilter(urlPatterns = "/*", initParams = {
        @WebInitParam(name="exclusions", value = "/login,/test")
})
public class CommonFilter extends HttpFilter {

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
        super.init(filterConfig);
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        //请求url
        String url = request.getRequestURI();
        if (false) {
            HttpSession session = request.getSession();
            String user = (String) session.getAttribute("user");
            if (StringUtils.isEmpty(user)) {
                response.sendRedirect("/login");
                return;
            }
        }
        super.doFilter(request, response, chain);
    }

    /**
     * 判断是否过滤
     * @param url   请求url
     * @return
     */
    private boolean isFilterUrl(String url) {
        boolean flag = false;
        if (!StringUtils.isEmpty(url)) {
            if (exclusionList.contains(url)) {
                flag = true;
            }
        }
        return flag;
    }

}
