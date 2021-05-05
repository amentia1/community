package com.community;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author flunggg
 * @date 2020/8/7 15:57
 * @Email: chaste86@163.com
 */
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class) // 以CommunityApplication.class配置的启动测试
public class KafkaTest {

    @Autowired
    private KafkaProducer kafkaProducer;

    @Test
    public void testKafka() throws InterruptedException {
        kafkaProducer.sendMessage("test", "您好！");
        kafkaProducer.sendMessage("test", "在吗？");

        Thread.sleep(10000);
    }
}

// 生产者
@Component
class KafkaProducer {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    /**
     * 主动发送
     * @param topic
     * @param content
     */
    public void sendMessage(String topic, String content) {
        kafkaTemplate.send(topic, content);
    }
}

// 消费者
@Component
class KafkaConsumer {

    /**
     * 被动接收，会有一个线程阻塞在text主题消息队列，一有消息就读取，否则就阻塞在那
     * 可能会有延迟
     * @param record 会被消息自动封装进去
     */
    @KafkaListener(topics = {"test"})
    public void heandleMesage(ConsumerRecord record) {
        System.out.println(record.value());
    }

}