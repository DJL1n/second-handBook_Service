package com.wjs.secondhandbook.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 1. 配置密码加密器
    // 我们将使用 BCrypt 算法对密码进行哈希加密，这是目前业界的标准做法
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2. 配置安全拦截规则
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // 允许匿名访问的路径：静态资源、注册页、登录页
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/register", "/login", "/").permitAll()
                        // 其他所有请求都需要登录才能访问
                        .anyRequest().authenticated()
                )
                // 配置表单登录（后续我们会自定义登录页）
                .formLogin(form -> form
                        .loginPage("/login") // 指定登录页面的路径
                        .permitAll()
                )
                // 暂时关闭 CSRF 保护，方便我们后续使用 Postman 或简单的表单测试 POST 请求
                .csrf(csrf -> csrf.disable());

        return http.build();
    }
}
