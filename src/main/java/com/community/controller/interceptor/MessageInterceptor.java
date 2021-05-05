package com.community.controller.interceptor;

import com.community.entity.User;
import com.community.service.MessageService;
import com.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 查询未读私信模块的数量和未读系统通知数量
 * @author flunggg
 * @date 2020/8/7 23:18
 * @Email: chaste86@163.com
 */
@Component
public class MessageInterceptor implements HandlerInterceptor {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private MessageService messageService;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if(user != null && modelAndView != null) {
            // 查询未读私信数量
            int unreadLetterCount = messageService.findUnreadLetterCount(user.getId(), null);
            // 查询未读系统通知数量
            int unreadNoticeCount = messageService.findUnreadNoticeCount(user.getId(), null);
            modelAndView.addObject("allUnreadCount", unreadLetterCount + unreadNoticeCount);
        }
    }
}
