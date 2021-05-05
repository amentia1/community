package com.community.controller.interceptor;

import com.community.entity.User;
import com.community.service.DataService;
import com.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author flunggg
 * @date 2020/8/10 10:02
 * @Email: chaste86@163.com
 */
@Component
public class DataInterceptor implements HandlerInterceptor {

    @Autowired
    private DataService dateService;

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 游客量（IP）
        String ip = request.getRemoteHost();
        dateService.addUV(ip);
        // 用户活跃量（用户Id）
        User user = hostHolder.getUser();
        if(user != null) {
            dateService.addDAU(user.getId());
        }

        return true;
    }
}
