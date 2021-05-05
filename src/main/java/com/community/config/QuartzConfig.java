package com.community.config;

import com.community.quartz.PostScoreRefreshJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

/**
 * 任务调度
 * 这里配置，配置文件也得配置，第一次被读取，会把信息放进数据库，以后访问数据库取调度任务
 * 否则，如果配置文件没有配置，那就只是在内存中
 * - beanFactory：容器的顶层接口。
 * - FactoryBean：可以简化Bean的实例化过程。
 *   - 1.通过FactoryBean封装了Bean的实例化过程
 *   - 2.可以将FactoryBean装配到spring容器里
 *   - 3.将FactoryBean注入到其他的Bean
 *   - 4.将Bean得到的是 FactoryBean管理的对象实例
 * 只要@Bean就会自动执行
 * @author flunggg
 * @date 2020/8/10 14:12
 * @Email: chaste86@163.com
 */
@Configuration
public class QuartzConfig {

    /*------- 测试 -----*/
    // 配置JobDetail
    // @Bean
    // public JobDetailFactoryBean myJobDetail() {
    //     JobDetailFactoryBean jobDetailFactoryBean = new JobDetailFactoryBean();
    //     jobDetailFactoryBean.setJobClass(MyJob.class);
    //     jobDetailFactoryBean.setName("MyJob");
    //     jobDetailFactoryBean.setGroup("MyJobGroup");
    //     jobDetailFactoryBean.setDurability(true); // 声明这个任务是否长久保持
    //     jobDetailFactoryBean.setRequestsRecovery(true); // 如果应用程序有问题，这个任务是否可被恢复
    //     return jobDetailFactoryBean;
    // }

    /**
     * 配置Trigger：
     * SimpleTriggerFactoryBean：简单的，比如每10分钟要执行一次
     * CronTriggerFactoryBean：复杂的，比如每个月月底半夜几点要做有个任务
     * @param myJobDetail 自动注入，跟同名的先注入进来
     * @return
     */
    // @Bean
    // public SimpleTriggerFactoryBean mySimpleTrigger(JobDetail myJobDetail) {
    //     SimpleTriggerFactoryBean simpleTriggerFactoryBean = new SimpleTriggerFactoryBean();
    //     simpleTriggerFactoryBean.setJobDetail(myJobDetail);
    //     simpleTriggerFactoryBean.setName("myTrigger");
    //     simpleTriggerFactoryBean.setGroup("myTriggerGroup");
    //     simpleTriggerFactoryBean.setRepeatInterval(3000); // 每隔多久执行，毫秒
    //     simpleTriggerFactoryBean.setJobDataMap(new JobDataMap()); // Trigger底层需要存入Job的状态，需要用什么来存，这指定一个对象
    //     return simpleTriggerFactoryBean;
    // }

    /*------- 论坛的热榜：每15s更新一次 -----*/

    /**
     * 配置JobDetail
     * 也就是配置定时的任务
     * @return
     */
    @Bean
    public JobDetailFactoryBean postScoreRefreshJobDetail() {
        JobDetailFactoryBean jobDetailFactoryBean = new JobDetailFactoryBean();
        jobDetailFactoryBean.setJobClass(PostScoreRefreshJob.class);
        jobDetailFactoryBean.setName("postScoreRefreshJob");
        jobDetailFactoryBean.setGroup("communityJobGroup");
        jobDetailFactoryBean.setDurability(true); // 声明这个任务是否长久保持
        jobDetailFactoryBean.setRequestsRecovery(true); // 如果应用程序有问题，这个任务是否可被恢复
        return jobDetailFactoryBean;
    }

    /**
     * 配置Trigger：
     * 配置定时任务的执行间隔
     * @param postScoreRefreshJobDetail 自动注入，要跟上面那个方法同名，才会先注入进来
     * @return
     */
    @Bean
    public SimpleTriggerFactoryBean postScoreRefreshSimpleTrigger(JobDetail postScoreRefreshJobDetail) {
        SimpleTriggerFactoryBean simpleTriggerFactoryBean = new SimpleTriggerFactoryBean();
        simpleTriggerFactoryBean.setJobDetail(postScoreRefreshJobDetail);
        simpleTriggerFactoryBean.setName("postScoreRefreshTrigger");
        simpleTriggerFactoryBean.setGroup("communityTriggerGroup");
        simpleTriggerFactoryBean.setRepeatInterval(1000 * 30); // 每15s更新一次
        simpleTriggerFactoryBean.setJobDataMap(new JobDataMap()); // Trigger底层需要存入Job的状态，需要用什么来存，这指定一个对象
        return simpleTriggerFactoryBean;
    }
}
