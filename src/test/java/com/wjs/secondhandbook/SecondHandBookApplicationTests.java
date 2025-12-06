package com.wjs.secondhandbook;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@SpringBootTest
class SecondHandBookApplicationTests {

    // 注入数据源对象，看看能不能拿到
    @Autowired
    DataSource dataSource;

    // 注入 JdbcTemplate，这是我们后续操作数据库的核心工具
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    void contextLoads() {
        // 这个空测试方法用于验证 Spring 容器能否正常启动。
        // 如果配置错误，启动时这里就会报错。
    }

    @Test
    void testConnection() throws SQLException {
        // 测试1: 查看数据源信息
        System.out.println("数据源对象类型: " + dataSource.getClass());
        Connection connection = dataSource.getConnection();
        System.out.println("成功获取数据库连接: " + connection);
        connection.close(); // 用完记得关闭（虽然连接池会托管，但好习惯要有）

        // 测试2: 简单查询一下 users 表里有多少条数据
        // 如果刚才SQL执行成功，这里应该输出 4
        Long userCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Long.class);
        System.out.println("数据库连接测试成功！当前 users 表中共有 " + userCount + " 名用户。");
    }

}
