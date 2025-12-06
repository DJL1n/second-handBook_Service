package com.wjs.secondhandbook.config;

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

    // 1. 修改这里：使用 NoOpPasswordEncoder (不加密，直接明文比对)
    @Bean
    public PasswordEncoder passwordEncoder() {
        // 这是一个过时的方法，但在测试和非生产环境中为了方便直接查看数据库密码非常有用
        return NoOpPasswordEncoder.getInstance();
    }

    // 2. 配置安全拦截规则 (保持不变)
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // 1. 允许匿名访问的路径：只有 静态资源、注册、登录
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/register", "/login").permitAll()
                        // (注意：把 "/" 和 "/books/**" 从上面删掉了！)

                        // 2. 其他所有请求（包括首页 "/"）都必须登录
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .permitAll()
                )
                .csrf(csrf -> csrf.disable());

        return http.build();
    }

}
