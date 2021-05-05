package com.community.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * kafka的消息封装成事件
 * 为了通用性，
 * @author flunggg
 * @date 2020/8/7 16:42
 * @Email: chaste86@163.com
 */
public class Event {
    private String topic;
    // 哪一个用户触发事件
    private int userId;
    // 用户触发事件的实体是：点赞？关注？评论？
    private int entityType;
    private int entityId;
    // 实体的作者是谁？
    private int entityUserId;
    // 为了一定的扩展性，未来可以存入额外数据
    private Map<String, Object> data = new HashMap<>();

    public String getTopic() {
        return topic;
    }

    // 可以这样 setXxx().setAaa().setBbb() 这样一连串
    // 虽然可以用构造器，但是构造器有时一些字段不需要，所以还是下面这样更灵活
    public Event setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public int getUserId() {
        return userId;
    }

    public Event setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public Event setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public Event setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityUserId() {
        return entityUserId;
    }

    public Event setEntityUserId(int entityUserId) {
        this.entityUserId = entityUserId;
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Event setData(String key, Object value) {
        this.data.put(key, value);
        return this;
    }


}
