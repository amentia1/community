package com.community.quartz;

import com.community.entity.DiscussPost;
import com.community.service.CommentService;
import com.community.service.DiscussPostService;
import com.community.service.ElasticsearchService;
import com.community.service.LikeService;
import com.community.util.CommunityConstant;
import com.community.util.RedisUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author flunggg
 * @date 2020/8/10 16:29
 * @Email: chaste86@163.com
 */
public class PostScoreRefreshJob implements Job, CommunityConstant {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostScoreRefreshJob.class);

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private CommentService commentService;

    // 帖子变化也得更新到ES
    @Autowired
    private ElasticsearchService elasticsearchService;

    // 一个固定的日期，用在与计算帖子发布时间的差
    private static final Date epoch;

    static {
        try {
            epoch = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2020-01-01 00:00:00");
        } catch (ParseException e) {
            throw new RuntimeException("初始化纪元失败");
        }
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String redisKey = RedisUtil.getPostScoreKey();
        // 每一个key都要算下，用BoundSetOperations
        BoundSetOperations operations = redisTemplate.boundSetOps(redisKey);
        if(operations.size() == 0) {
            LOGGER.info("[任务取消！] 没有需要刷新的帖子分数");
            return ;
        }

        LOGGER.info("[任务开始！] 正在刷新的帖子分数:" + operations.size());

        // 计算每个变化帖子的分数
        while(operations.size() > 0) {
            // 这里会把这个集合中的值弹出来，直到弹光了。
            this.refresh((Integer) operations.pop());
        }

        LOGGER.info("[任务结束！] 帖子分数刷新完毕！");
    }

    private void refresh(Integer postId) {
        // 查到帖子
        DiscussPost discussPost = discussPostService.findDiscussPostById(postId);

        if(discussPost == null || discussPost.getStatus() == 2) {
            LOGGER.error("[任务取消！] 帖子不存在：id=" + postId);
            return;
        }

        // 是否加精
        boolean wonderful = discussPost.getStatus() == 1;
        // 点赞数量
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, discussPost.getId());
        // 帖子评论数量（不计算楼中楼）
        int commentCount = commentService.findCountByEntity(ENTITY_TYPE_POST, discussPost.getId());
        // 计算分数并更新到帖子中
        // 先计算权重
        double w = (wonderful? 75 : 0) + likeCount * 2 + commentCount * 10;
        double score = Math.log10(Math.max(w, 1)) + (discussPost.getCreateTime().getTime() - epoch.getTime());
        discussPostService.updateScore(postId, score);
        // 同时更新到搜索引擎中
        discussPost.setScore(score);
        elasticsearchService.save(discussPost);
    }
}
