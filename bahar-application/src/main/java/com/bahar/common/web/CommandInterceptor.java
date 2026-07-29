package com.bahar.common.web;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by FSQ
 * CopyRight https://www.bahar.cn
 */
public class CommandInterceptor implements AsyncHandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String serverName = request.getServerName();
        if (!StringUtils.isEmpty(serverName) && serverName.equals("localhost")) {
            return true;
        } else {
            return false;
        }
    }
}
