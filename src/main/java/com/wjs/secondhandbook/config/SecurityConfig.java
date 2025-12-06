package com.wjs.secondhandbook.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // 1. 静态资源 + 首页 + 商品详情 + 图片上传路径 -> 统统放行
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/uploads/**").permitAll()
                        .requestMatchers("/", "/product/**").permitAll()
                        // 2. 只有原本的API和个人中心需要登录
                        .requestMatchers("/my-shelf", "/books/buy/**").authenticated()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login") // 这里只作为后备，主要靠弹窗
                        .loginProcessingUrl("/api/login") // 前端弹窗POST这个地址
                        // --- 关键点：登录成功不跳转，返回 JSON ---
                        .successHandler((req, resp, auth) -> {
                            resp.setContentType("application/json;charset=utf-8");
                            resp.getWriter().write("{\"code\": 200, \"message\": \"登录成功\"}");
                        })
                        // --- 关键点：登录失败不跳转，返回 JSON ---
                        .failureHandler((req, resp, ex) -> {
                            resp.setContentType("application/json;charset=utf-8");
                            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            resp.getWriter().write("{\"code\": 401, \"message\": \"账号或密码错误\"}");
                        })
                        .permitAll()
                )
                // 遇到未登录的情况，不跳转302，而是返回401（方便前端判断弹窗）
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, resp, authException) -> {
                            // 如果是 AJAX 请求，返回 401
                            if ("XMLHttpRequest".equals(req.getHeader("X-Requested-With"))) {
                                resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                            } else {
                                // 如果是普通浏览器访问受限页面，还是跳登录页
                                resp.sendRedirect("/login");
                            }
                        })
                )
                .csrf(csrf -> csrf.disable());

        return http.build();
    }
}
