package com.community.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 启动spring的线程池：ThreadPoolTaskExecutor和ThreadPoolTaskScheduled
 * 否则会报错：Unsatisfied dependency expressed through field 'threadPoolTaskScheduler'; nested exception is org.springframework.beans.factory.NoSuchBeanDefinitionException: No qualifying bean of type 'org.springframework.scheduling.concurrent.
 *              ThreadPoolTaskScheduler' available: expected at least 1 bean which qualifies as autowire candidate. Dependency annotations: {@org.springframework.beans.factory.annotation.Autowired(required=true)}
 * 需要在这加上：@EnableScheduling
 * 后面引入Spring Quartz 就不用
 *
 * @EnableAsync： 这个注解可以让该方法在多线程环境下被当作任务异步调用，主要在某个方法上加上@Async注解即可
 * 使用的时候直接调用该方法就自动启动线程池。这里是普通线程池
 *
 * 定时线程池：多线程的定时任务会自动被调用，例子：在某方法上这样写：@Scheduled(initialDelay = 5000, fixedDelay = 1000)
 * @author flunggg
 * @date 2020/8/10 11:57
 * @Email: chaste86@163.com
 */
@Configuration
@EnableScheduling
@EnableAsync
public class ThreadPoolConfig {
}
