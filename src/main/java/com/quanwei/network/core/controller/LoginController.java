package com.quanwei.network.core.controller;

import com.quanwei.network.core.Enum.ErrorCodeEnum;
import com.quanwei.network.core.entity.ResponseEntity;
import com.quanwei.network.core.util.DataUtil;
import com.quanwei.network.core.util.ParamUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
public class LoginController extends BaseController {

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView login() {
        return new ModelAndView("login.html");
    }

    /**
     * 登入
     * @param request
     *  username: 用户名
     *  password: 密码
     * @param response
     */
    @RequestMapping(value = "/login")
    public void login(HttpServletRequest request, HttpServletResponse response) {
        ResponseEntity responseEntity = new ResponseEntity(ErrorCodeEnum.USERNOTEXIST);
        Map<String, Object> paramMap = ParamUtil.getMapData(request);
        String username = ParamUtil.getRequiredString(paramMap, "username");
        String password = ParamUtil.getRequiredString(paramMap, "password");
        Map<String, Object> user = DataUtil.getUser(username);
        if (user != null && user.size() > 0) {
            if (password.equalsIgnoreCase((String) user.get(username))) {
                request.getSession().setAttribute("user", user);
                responseEntity = new ResponseEntity(ErrorCodeEnum.SUCCESS);
            } else {
                logger.info("密码错误");
            }
        } else {
            logger.info("账号不存在");
        }
        renderJson(response, responseEntity);
    }

    /**
     * 登出
     * @param request
     * @param response
     */
    @RequestMapping(value = "/logout")
    public ModelAndView logout(HttpServletRequest request, HttpServletResponse response) {
        request.getSession().removeAttribute("user");
        return new ModelAndView("login.html");
    }

    @RequestMapping("test")
    public void test(@RequestBody Map<String, String> dataMap) {
        for (String key : dataMap.keySet()) {
            logger.info(key + ":" + dataMap.get(key));
        }
    }

}
