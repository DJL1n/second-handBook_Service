package com.wjs.secondhandbook.service;

import com.wjs.secondhandbook.model.User;
import com.wjs.secondhandbook.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

// 注意：删掉了 java.time.LocalDateTime 的引用，因为用不到了

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(User user) {
        // 1. 检查用户名是否存在
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("用户名已存在！");
        }

        // 2. 密码处理
        // 注意：因为你在 SecurityConfig 配置的是 NoOpPasswordEncoder (明文)，
        // 所以这里的 .encode() 其实不会加密，它会直接返回明文，这符合你的要求。
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        // 3. 补全默认信息
        user.setEnabled(true); // 注册的用户默认允许登录

        // 🔥 修正点1：删掉了 user.setCreatedAt(...)，因为表里没有这个字段了！

        // 🔥 修正点2：默认角色改为 "USER" (对应 data.sql 里的设定)，而不是 "BUYER"
        if (user.getRole() == null) {
            user.setRole("USER");
        }

        // 4. 保存到数据库
        userRepository.save(user);
    }
}
