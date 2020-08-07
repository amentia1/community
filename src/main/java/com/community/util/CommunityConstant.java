package com.community.util;

/**
 * @author flunggg
 * @date 2020/7/21 18:10
 * @Email: chaste86@163.com
 */
public interface CommunityConstant {

    /**
     * 激活成功
     */
    int ACTIVATION_SUCCESS = 0;

    /**
     * 重复激活
     */
    int ACTIVATION_REPEAT = 1;

    /**
     * 激活失败
     */
    int ACTIVATION_FAILURE = 2;

    /**
     * 默认的登录凭证超时时间
     */
    int DEFAULT_EXPIRED_SECONDS = 3600; // 一小时

    /**
     * 记住状态的登录凭证超时时间
     */
    int REMEMBER_EXPIRED_SECONDS = 3600 * 12; // 一天

    /**
     * 实体类型：帖子
     */
    int ENTITY_TYPE_COMMENT = 1;

    /**
     * 实体类型：回复
     */
    int ENTITY_TYPE_REPLY = 2;

    /**
     * 实体类型：用户
     */
    int ENTITY_TYPE_USER = 3;
}
