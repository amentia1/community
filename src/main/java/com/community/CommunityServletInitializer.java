package com.community;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * @author flunggg
 * @date 2020/8/12 21:27
 * @Email: chaste86@163.com
 */
public class CommunityServletInitializer extends SpringBootServletInitializer {
    /**
     * 需要部署到tomcat中，但是tomcat本身就有main方法，如果直接放到tomcat不行，所以这里在写一个接口用来启动项目。
     * @param builder
     * @return
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(CommunityApplication.class);
    }
}
