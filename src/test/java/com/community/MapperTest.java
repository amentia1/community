package com.community;

import com.community.dao.*;
import com.community.entity.DiscussPost;
import com.community.entity.LoginTicket;
import com.community.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;

/**
 * @author flunggg
 * @date 2020/7/18 20:40
 * @Email: chaste86@163.com
 */
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class) // 以CommunityApplication.class配置的启动测试
public class MapperTest {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private LoginTicketMapper loginTicketMapper;




    /*----------------------------UserMapper---------------------------------------*/
    @Test
    public void testSelectUser() {
        User user = userMapper.selectUserById(11);
        System.out.println(user);

        User liubei = userMapper.selectUserByName("liubei");
        System.out.println(liubei);

        User user1 = userMapper.selectUserByEmail("nowcoder113@sina.com");
        System.out.println(user1);

    }

    @Test
    public void testInsertUser() {
        User user = new User();
        user.setUsername("zhangsan");
        user.setPassword("123456");
        user.setEmail("123@163.com");
        user.setSalt("abc");
        user.setHeaderUrl("http://www.nowcoder.com/101.png");
        user.setCreateTime(new Date());
        int i = userMapper.insertUser(user);
        System.out.println(i);
        System.out.println(user.getId());
    }

    @Test
    public void testUpdateUser() {
        int i = userMapper.updateStatus(150, 1);
        System.out.println(i);
        int i1 = userMapper.updatePassword(150, "654321");
        System.out.println(i1);
        int i2 = userMapper.updateHeader(150, "http://www.nowcoder.com/100.png");
        System.out.println(i2);
    }

    /*------------------------DiscussPostMapper-----------------------------*/
    @Test
    public void testSelectPost() {
        // 查询
        // List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(149, 0, 5);
        // for(DiscussPost discussPost : discussPosts) {
        //     System.out.println(discussPost);
        // }
        //
        // int i = discussPostMapper.selectDiscussPostRows(149);
        // System.out.println(i);

        // 插入测试
        // DiscussPost discussPost = new DiscussPost();
        // discussPost.setUserId(101);
        // discussPost.setContent("1111");
        // discussPost.setCreateTime(new Date());
        // discussPost.setTitle("111");
        // discussPost.setType(0);
        // discussPostMapper.insertDiscussPost(discussPost);

        // 根据id查询
        DiscussPost discussPost = discussPostMapper.selectDiscussPostById(284);
        System.out.println(discussPost);

    }

    /*---------------------LoginTicketMapper--------------------------------*/
    @Test
    public void testInsertTicket() {
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setTicket("abc");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000 * 60 * 10));
        loginTicketMapper.insertLoginTicket(loginTicket);
    }
    @Test
    public void testSelectTicket() {
        LoginTicket abc = loginTicketMapper.selectLoginTicketByTicket("abc");
        System.out.println(abc);
        // 修改status
        loginTicketMapper.updateStatus("abc", 1);
        abc = loginTicketMapper.selectLoginTicketByTicket("abc");
        System.out.println(abc);
    }

    /*---------------comment----------------------------*/

}
