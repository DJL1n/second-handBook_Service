package com.wjs.secondhandbook.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. 权限配置
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                        .requestMatchers("/", "/index", "/register", "/api/login").permitAll()
                        .requestMatchers("/error").permitAll() // 必须放行错误页
                        .anyRequest().authenticated()
                )

                // 2. 登录配置
                .formLogin(form -> form
                        .loginPage("/")
                        .loginProcessingUrl("/api/login")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/?error")
                        .permitAll()
                )

                // 3. 登出配置
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .permitAll()
                )

                // 4. 关闭 CSRF (新写法)
                .csrf(AbstractHttpConfigurer::disable)

                // 5. 🔥 修复报错点：Headers 的新写法
                // 以前是 headers.frameOptions().disable() -> 报错
                // 现在必须写成 headers.frameOptions(frame -> frame.disable())
                .headers(headers -> headers
                        .frameOptions(frame -> frame.disable())
                );

        return http.build();
    }

    // 明文密码匹配器
    @SuppressWarnings("deprecation")
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}
