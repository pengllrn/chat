package com.yeta.chat.stomp;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @author YETA
 * @date 2018/11/23/0:31
 */
@Controller
public class PageController {

    /**
     * 登陆验证成功返回
     * @return
     */
    @GetMapping(value = "/slogin")
    public String slogin() {
        return "index.html";
    }

    /**
     * 登陆验证失败返回
     * @param request
     * @return
     */
    @GetMapping(value = "/flogin")
    public String flogin(HttpServletRequest request) {
        AuthenticationException authenticationException = (AuthenticationException) request.getSession().getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        return authenticationException.getMessage();
    }
}
