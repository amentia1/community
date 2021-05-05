package com.community;

import java.sql.*;

/**
 * @author flunggg
 * @date 2021/3/30 9:41
 * @Email: chaste86@163.com
 */
public class MySQLConnectionTest {

    // 8版本以上
    static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String URL = "jdbc:mysql://localhost:3306/community?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    // 8版本以下
    // static final String DRIVER = "com.mysql.Driver";
    // static final String URL = "jdbc:mysql://localhost:3306/community";

    static final String USER = "root";
    static final String PASSWORD = "86891730";

    public static void main(String[] args) {
        Connection con = null;
        Statement sta = null;
        // 以递归的形式关闭资源
        try {
            // 1. 注册驱动
            Class.forName(DRIVER);
            // 2. 建立连接
            System.out.println("数据库连接....");
            con = DriverManager.getConnection(URL, USER, PASSWORD);
            // 3. 实例化Statement对象
            System.out.println("实例化Statement对象");
            sta = con.createStatement();
            // 4. 执行SQL
            String sql = "select * from user";
            ResultSet rs = sta.executeQuery(sql);

            while (rs.next()) {
                int id = rs.getInt("id");
                String user = rs.getString("username");

                System.out.println("id:" + id + ", user:" +user);
            }
            // 5. 关闭
            rs.close();
            sta.close();
            con.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            // 关闭资源
            try {
                if (sta != null) {
                    sta.close();
                }
            } catch (SQLException throwables) {

            }

            // 关闭资源
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException throwables) {

            }
        }
        System.out.println("数据库连接结束");
    }
}
