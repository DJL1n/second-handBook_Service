package com.wjs.secondhandbook.service;

import com.wjs.secondhandbook.model.User;
import com.wjs.secondhandbook.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 构造器注入，Spring 官方推荐方式
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(User user) {
        // 1. 检查用户名是否存在
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("用户名已存在！");
        }

        // 2. 密码加密 (非常重要，不能存明文)
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        // 3. 补全默认信息
        user.setCreatedAt(LocalDateTime.now());
        if (user.getRole() == null) {
            user.setRole("BUYER"); // 默认角色
        }

        // 4. 保存到数据库
        userRepository.save(user);
    }
}
