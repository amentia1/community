package com.community.event;

import com.alibaba.fastjson.JSONObject;
import com.community.entity.DiscussPost;
import com.community.entity.Event;
import com.community.entity.Message;
import com.community.service.DiscussPostService;
import com.community.service.ElasticsearchService;
import com.community.service.MessageService;
import com.community.util.CommunityConstant;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 消息队列---消费者
 * @author flunggg
 * @date 2020/8/7 16:56
 * @Email: chaste86@163.com
 */
@Component
public class EventConsumer implements CommunityConstant {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventConsumer.class);

    @Autowired
    private MessageService messageService;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private ElasticsearchService elasticsearchService;

    /**
     * 可以 一个方法消费一个主题
     * 也可以 一个方法消费多个主题
     * 也可以 一个主题可以被多个方法消费
     * <p>
     * 点赞，关注，评论：三个通知的形式很相似，所以就使用一个方法
     * @param record 会自动接收topics传来的消息，然后封装进ConsumerRecord
     */
    @KafkaListener(topics = {TOPIC_LIKE, TOPIC_FOLLOW, TOPIC_COMMENT})
    public void handleCommentMessage(ConsumerRecord record) {
        if(record == null || record.value() == null) {
            LOGGER.error("消息内容为空！");
            return ;
        }
        // 把JSON字符串转为Event对象
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if(event == null) {
            LOGGER.error("消息格式错误！");
            return ;
        }

        // 发送站内通知
        Message message = new Message();
        // 表示系统通知
        message.setFromId(SYSTEM_USER_ID);
        message.setToId(event.getEntityUserId());
        message.setConversationId(event.getTopic());
        message.setCreateTime(new Date());

        Map<String, Object> content = new HashMap<>();
        content.put("userId", event.getUserId());
        content.put("entityType", event.getEntityType());
        content.put("entityId", event.getEntityId());

        // 可能额外数据
        if(!event.getData().isEmpty()) {
            for(Map.Entry<String, Object> entry : event.getData().entrySet()) {
                content.put(entry.getKey(), entry.getValue());
            }
        }

        message.setContent(JSONObject.toJSONString(content));

        // 存入数据库
        messageService.insertMessage(message);
    }

    /**
     * 消费发帖事件
     * @param record
     */
    @KafkaListener(topics = {TOPIC_PUBLISH})
    public void handlePublishMessage(ConsumerRecord record) {
        if(record == null || record.value() == null) {
            LOGGER.error("消息内容为空！");
            return ;
        }
        // 把JSON字符串转为Event对象
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if(event == null) {
            LOGGER.error("消息格式错误！");
            return ;
        }

        // 1. 根据事件中传过来的entityId，这一定是帖子ID,查出这个帖子
        DiscussPost discussPost = discussPostService.findDiscussPostById(event.getEntityId());
        // 2. 把帖子存入ES服务器
        elasticsearchService.save(discussPost);

    }

    /**
     * 删除发帖事件
     * @param record
     */
    @KafkaListener(topics = {TOPIC_DELETE})
    public void handleDeleteMessage(ConsumerRecord record) {
        if(record == null || record.value() == null) {
            LOGGER.error("消息内容为空！");
            return ;
        }
        // 把JSON字符串转为Event对象
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if(event == null) {
            LOGGER.error("消息格式错误！");
            return ;
        }

        // 把帖子从ES服务器删除
        elasticsearchService.deleteDiscussPost(event.getEntityId());

    }
}
