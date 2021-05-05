package com.community.service;

import com.community.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 统计访问量和用户活跃量
 * @author flunggg
 * @date 2020/8/9 23:58
 * @Email: chaste86@163.com
 */
@Service
public class DataService {

    @Autowired
    private RedisTemplate redisTemplate;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

    /**
     * 以年月日划分，将指定的ip存入UV
     * @param ip 游客的ip
     */
    public void addUV(String ip) {
        String redisKey = RedisUtil.getUVKey(simpleDateFormat.format(new Date()));
        redisTemplate.opsForHyperLogLog().add(redisKey, ip);
    }

    /**
     * 统计日期范围内游客量
     * @param start 开始时间
     * @param end 结束时间
     * @return
     */
    public long calculateUV(Date start, Date end) {
        if(start == null || end == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        // 需要从start遍历到end，取出每天的游客量
        List<String> keyList = new ArrayList<>();
        // 要对日期操作使用下面类：
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        // start 不晚于 end
        while(!calendar.getTime().after(end)) {
            String key = RedisUtil.getUVKey(simpleDateFormat.format(calendar.getTime()));
            keyList.add(key);
            calendar.add(Calendar.DATE,1);
        }

        // 范围
        String redisKey = RedisUtil.getUVKey(simpleDateFormat.format(start), simpleDateFormat.format(end));
        // 合并
        redisTemplate.opsForHyperLogLog().union(redisKey, keyList.toArray());
        // 统计
        return redisTemplate.opsForHyperLogLog().size(redisKey);
    }

    /**
     * 以年月日划分，添加日活跃用户
     * @param userId
     */
    public void addDAU(int userId) {
        String redisKey = RedisUtil.getDAUKey(simpleDateFormat.format(new Date()));
        redisTemplate.opsForValue().setBit(redisKey, userId, true);
    }

    /**
     * 统计日期范围内的活跃用户
     * @param start
     * @param end
     * @return
     */
    public long calculateDAU(Date start, Date end) {
        if(start == null || end == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        // 需要从start遍历到end，取出每天的游客量
        List<byte[]> keyList = new ArrayList<>();
        // 要对日期操作使用下面类：
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        // start 不晚于 end
        while(!calendar.getTime().after(end)) {
            String key = RedisUtil.getDAUKey(simpleDateFormat.format(calendar.getTime()));
            keyList.add(key.getBytes());
            calendar.add(Calendar.DATE,1);
        }

        return (long) redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                String redisKey = RedisUtil.getDAUKey(simpleDateFormat.format(start), simpleDateFormat.format(end));
                // 进行OR操作，只要有一天登录就行
                connection.bitOp(RedisStringCommands.BitOperation.OR, redisKey.getBytes(), keyList.toArray(new byte[0][0]));
                // 统计
                return connection.bitCount(redisKey.getBytes());
            }
        });
    }
}
