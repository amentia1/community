package com.community;

import com.community.dao.UserMapper;
import com.community.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.sql.SQLException;

/**
 * @author flunggg
 * @date 2020/10/8 18:00
 * @Email: chaste86@163.com
 */
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class) // 以CommunityApplication.class配置的启动测试
public class JDBCTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void test() throws SQLException {
        User user = userMapper.selectUserByName("x ' or 1 = 1");
        System.out.println(user);

    }
}
