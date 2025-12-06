package com.wjs.secondhandbook.service;

import com.wjs.secondhandbook.model.User;
import com.wjs.secondhandbook.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. 去数据库查用户
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));

        // 2. 将数据库的 User 对象转换为 Spring Security 认识的 UserDetails 对象
        // 注意：这里需要处理一下角色，通常 Spring Security 要求角色以 "ROLE_" 开头
        String roleName = user.getRole().startsWith("ROLE_") ? user.getRole() : "ROLE_" + user.getRole();

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(), // 这里的密码是数据库里加密后的
                Collections.singletonList(new SimpleGrantedAuthority(roleName)) // 设置权限
        );
    }
}
