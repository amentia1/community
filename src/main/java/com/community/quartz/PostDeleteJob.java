package com.community.quartz;

import com.community.service.DiscussPostService;
import com.community.util.CommunityConstant;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author flunggg
 * @date 2020/8/13 9:02
 * @Email: chaste86@163.com
 */
public class PostDeleteJob implements Job, CommunityConstant {
    @Autowired
    private DiscussPostService discussPostService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
    }
}
