package com.community;

import org.junit.jupiter.api.Test;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author flunggg
 * @date 2020/8/10 14:40
 * @Email: chaste86@163.com
 */
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class) // 以CommunityApplication.class配置的启动测试
public class QuartzTest {

    @Autowired
    private Scheduler scheduler;

    // 删除数据库中的Job
    @Test
    public void testDeleteJob() throws SchedulerException {
        boolean b = scheduler.deleteJob(new JobKey("postScoreRefreshJob", "communityJobGroup"));
        System.out.println(b);
    }
}
