package com.community;

import com.community.service.MyService;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author flunggg
 * @date 2020/8/10 10:51
 * @Email: chaste86@163.com
 */
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class) // 以CommunityApplication.class配置的启动测试
public class ThreadPoolTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadPoolTest.class);

    // JDK普通线程池
    // 会复用这5个线程
    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    // JDK可执行定时任务的线程池
    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);

    // 需要写一个配置类
    // Spring普通线程池
    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    // Spring定时线程池
    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    @Autowired
    private MyService myService;


    public void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testExecutorService() {
        Runnable task = () -> LOGGER.debug("Hello ExecutorService!");

        for(int i = 1; i < 10; i++) {
            // 只会在5个线程内切换执行任务
            executorService.submit(task);
        }

        sleep(200);
    }

    @Test
    public void testScheduledExecutorService() {
        Runnable task = () -> LOGGER.debug("Hello ScheduledExecutorService!");

        // scheduledExecutorService.scheduleWithFixedDelay() // 执行一次就结束
        // 参数：任务，延迟多少毫秒才执行，时间间隔，时间单位
        scheduledExecutorService.scheduleAtFixedRate(task, 10000, 1000, TimeUnit.MILLISECONDS); // 执行多次
        sleep(30000);
    }

    // spring的线程池，但是需要在配置文件配置下线程池的一些东西
    // 如果要用，优先使用这个，因为可以配置最大线程池数量，而且超出时的等待队列数量
    @Test
    public void testThreadPoolTaskExecutor() {
        Runnable task = () -> LOGGER.debug("Hello ThreadPoolTaskExecutor!");
        // 跟JDK一样
        for(int i = 1; i < 10; i++) {
            threadPoolTaskExecutor.submit(task);
        }

        sleep(10000);
    }

    // spring的定时线程池，需要在配置文件配置下线程池的一些东西
    @Test
    public void testThreadPoolTaskScheduler() {
        Runnable task = () -> LOGGER.debug("Hello ThreadPoolTaskScheduler!");

        Date startTime = new Date(System.currentTimeMillis() + 10000);
        // 毫秒
        threadPoolTaskScheduler.scheduleAtFixedRate(task, startTime, 1000);
        sleep(30000);
    }

    // 5. spring普通线程池的简化方式
    @Test
    public void testThreadPoolTaskExecutorSimple() {
        for (int i = 0; i < 10; i++) {
            // 以多线程的方式取调用这个任务
            myService.execute1();
        }

        sleep(10000);
    }

    // spring的定时线程池，需要在配置文件配置下线程池的一些东西
    @Test
    public void testThreadPoolTaskSchedulerSimple() {
        // 会自动被调用

        sleep(30000);
    }
}
