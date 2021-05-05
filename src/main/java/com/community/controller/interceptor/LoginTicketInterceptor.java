package com.community.controller.interceptor;

import com.community.entity.LoginTicket;
import com.community.entity.User;
import com.community.service.LoginTicketService;
import com.community.service.UserService;
import com.community.util.CookieUtil;
import com.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * 登录状态下，每次请求，顺便查出用户信息
 * @author flunggg
 * @date 2020/7/23 15:01
 * @Email: chaste86@163.com
 */
@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Autowired
    private LoginTicketService loginTicketService;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;
    // controller前执行
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Cookie cookie = CookieUtil.getValue(request, "ticket");
        if (cookie != null) {
            String ticket = cookie.getValue();

            // 查询凭证
            LoginTicket loginTicketByTicket = loginTicketService.findLoginTicketByTicket(ticket);
            // 取出的时间，after（晚于）当前时间
            if (loginTicketByTicket != null && loginTicketByTicket.getStatus() == 0
                    && loginTicketByTicket.getExpired().after(new Date())) {
                // 查询用户
                User user = userService.findUserById(loginTicketByTicket.getUserId());
                // 在本次请求中持有用户，考虑到Session具有共享数据，而这里是多个浏览器发出的请求，得让线程之间隔离，使用ThraedLocal来存储
                hostHolder.setUser(user);


                // 引入Spring Security
                // 构建用户认证的结果，并存入SecurityContext，以便Security授权
                // 第一个参数: 认证的主要信息
                // 第二个参数: 证书/认证凭证(密码或者能代替密码的东西)
                // 第三个参数: 当前用户的权限
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        user, user.getPassword(), userService.getAuthorities(user.getId()));
                // 认证成功后，认证结果会通过SecurityContextHolder存入SecurityContext中
                SecurityContextHolder.setContext(new SecurityContextImpl(authentication));
            }

        }


        return true;
    }
    // controller后执行
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

        User user = hostHolder.getUser();
        if (user != null && modelAndView != null) {
            // 前端可以显示登录用户信息
            modelAndView.addObject("loginUser", user);
        }
    }

    // 在TemplateEngine之后才执行
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
    }
}
