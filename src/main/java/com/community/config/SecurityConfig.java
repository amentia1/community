package com.community.config;

import com.community.util.CommunityConstant;
import com.community.util.CommunityUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author flunggg
 * @date 2020/8/9 15:45
 * @Email: chaste86@163.com
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter implements CommunityConstant {

    // 忽略静态资源，因为不需要保密不需要Spring Security管理，所以直接放行。
    @Override
    public void configure(WebSecurity web) throws Exception {
        // 虽然是把resources下全忽略了，但是对于需要拦截的下面会设置
        web.ignoring().antMatchers("/resources/**");
    }

    // 登录认证就用原先的

    // 这里只搞权限
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 先搞都可以访问的
        http.authorizeRequests()
                .antMatchers(
                        "/user/setting",
                        "/user/upload",
                        "/discuss/add",
                        "comment/add/**",
                        "letter/**",
                        "notice/**",
                        "/like",
                        "/follow",
                        "/unfollow"
                )
                .hasAnyAuthority(
                        AUTHOURTTY_USER,
                        AUTHOURTTY_ADMIN,
                        AUTHOURTTY_MODERATOR
                )
                .antMatchers( // 版主有 置顶和加精和删除的权限
                        "/discuss/top",
                        "/discuss/wonderful"
                )
                .hasAnyAuthority(
                        AUTHOURTTY_MODERATOR
                )
                .antMatchers( // 管理员有 删除帖子的权限
                        "/discuss/delete",
                        "/data/**"
                )
                .hasAnyAuthority(
                        AUTHOURTTY_ADMIN,
                        AUTHOURTTY_MODERATOR
                )
                .anyRequest().permitAll() // 其他任何请求统统都允许
                .and().csrf().disable(); // 可以禁用csrf
        // 权限不够的处理
        http.exceptionHandling()
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    // 没有登录
                    @Override
                    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
                        String xRequestedWith = request.getHeader("x-requested-with");
                        if ("XMLHttpRequest".equals(xRequestedWith)) {
                            // 异步请求，提示需要登录信息
                            // 声明要返回的类型，这里设置为存文本的形式，浏览器获取这种不会处理，得前端人工转为JSON
                            response.setContentType("application/plain;charset=utf-8");
                            PrintWriter writer = response.getWriter();
                            writer.write(CommunityUtil.getJSONString(403, "您还没有登录哦！请登录"));
                        } else {
                            // 同步请求，就直接返回到登录页面
                            response.sendRedirect(request.getContextPath() + "/login");
                        }
                    }
                })
                .accessDeniedHandler(new AccessDeniedHandler() {
                    // 已经登录但权限不足
                    @Override
                    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
                        String xRequestedWith = request.getHeader("x-requested-with");
                        if ("XMLHttpRequest".equals(xRequestedWith)) {
                            // 异步请求，提示需要登录信息
                            // 声明要返回的类型，这里设置为存文本的形式，浏览器获取这种不会处理，得前端人工转为JSON
                            response.setContentType("application/plain;charset=utf-8");
                            PrintWriter writer = response.getWriter();
                            writer.write(CommunityUtil.getJSONString(403, "您还没有访问此功能的权限"));
                        } else {
                            // 同步请求，就直接返回到登录页面
                            response.sendRedirect(request.getContextPath() + "/denied");
                        }
                    }
                });

        // Spring Security默认检测到/logout就会自动拦截，自动处理退出
        // 它是在Controller之前处理的，当处理完就不会往下执行，而我这里是退出后是跳转到首页，这不符合
        // 可以自己重写退出后的逻辑：logoutSuccessHandler和new LogoutSuccessHandler()在里面写
        // 但是可以自己设置 检测退出的路径，设置一条项目中不会用到的路径来给Spring Security做拦截退出处理，
        //      相当于骗它去检测到这条路径后做退出处理
        http.logout()
                .logoutUrl("/securitylogout"); // 这路径项目不会用到


    }
}
