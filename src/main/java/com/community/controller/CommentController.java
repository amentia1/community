package com.community.controller;

import com.community.entity.Comment;
import com.community.entity.DiscussPost;
import com.community.entity.Event;
import com.community.event.EventProducer;
import com.community.service.CommentService;
import com.community.service.DiscussPostService;
import com.community.util.CommunityConstant;
import com.community.util.HostHolder;
import com.community.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

/**
 * @author flunggg
 * @date 2020/7/30 9:15
 * @Email: chaste86@163.com
 */
@Controller
@RequestMapping("/comment")
public class CommentController implements CommunityConstant {

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 添加帖子评论
     * @param discussPostId 重定向需要的id
     * @param comment 需要自己补充useId，date，status
     * @return 评论后重定向到当前帖子，所以需要discussPostId
     */
    @PostMapping("/add/{discussPostId}")
    public String addComment(@PathVariable("discussPostId")int discussPostId, Comment comment) {
        if(hostHolder.getUser() == null) {
            return "redirect:/login";
        }
        // 这里如果没有登录会出错，后面会统一处理
        comment.setUserId(hostHolder.getUser().getId());
        comment.setCreateTime(new Date());
        comment.setStatus(0);

        commentService.addComment(comment);

        // 评论后，触发评论事件，去通知被评论的用户
        // 需要带上帖子id，因为如果是楼中楼评论，entityId只能知道是哪个评论，不能知道拿找帖子。
        Event event = new Event()
                .setTopic(TOPIC_COMMENT)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(comment.getEntityType())
                .setEntityId(comment.getEntityId())
                .setData("postId", discussPostId);

        // 评论有两种，一种是帖子中评论，一种是评论中回复
        if(comment.getEntityType() == ENTITY_TYPE_POST) {
            DiscussPost target = discussPostService.findDiscussPostById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        } else if(comment.getEntityType() == ENTITY_TYPE_COMMENT) {
            Comment target = commentService.findCommentById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }
        // 存进消息队列
        eventProducer.fireEvent(event);

        // 引入ES，只是让帖子中的评论加入ES
        if(comment.getEntityType() == ENTITY_TYPE_POST) {
            event = new Event()
                    .setTopic(TOPIC_PUBLISH)
                    .setUserId(comment.getUserId())
                    .setEntityType(ENTITY_TYPE_POST) // 这里肯定是帖子，所有直接传
                    .setEntityId(discussPostId);
            eventProducer.fireEvent(event);


        }
        // 排行榜计算分数
        // 只要评论就计算
        String redisKey = RedisUtil.getPostScoreKey();
        redisTemplate.opsForSet().add(redisKey, discussPostId);

        return "redirect:/discuss/detail/" + discussPostId;
    }
}
