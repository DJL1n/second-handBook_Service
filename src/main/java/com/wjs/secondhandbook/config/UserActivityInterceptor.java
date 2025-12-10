package com.wjs.secondhandbook.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest; // Spring Boot 3+ 用这个
import jakarta.servlet.http.HttpServletResponse;
// 如果是 Spring Boot 2，用 import javax.servlet.http.*;

import java.time.LocalDateTime;

@Component
public class UserActivityInterceptor implements HandlerInterceptor {

    @Autowired
    private JdbcTemplate jdbcTemplate; // 🔥 直接注入 JDBC 工具

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // 判断用户是否已登录
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            String username = auth.getName();

            // 🔥 直接写 SQL 更新时间
            String sql = "UPDATE users SET last_active_at = ? WHERE username = ?";

            // 执行更新
            jdbcTemplate.update(sql, LocalDateTime.now(), username);
        }
        return true;
    }
}
