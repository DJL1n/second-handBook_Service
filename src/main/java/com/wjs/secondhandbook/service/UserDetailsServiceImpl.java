package com.wjs.secondhandbook.service;

import com.wjs.secondhandbook.model.User;
import com.wjs.secondhandbook.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. 调用你写的 Repository 查询数据库
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));

        // 2. 将你的 User 转换成 Spring Security 的 UserDetails
        // 注意：这里的 user.getPassword() 取出来的是明文，因为你的数据库存的是明文
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getEnabled(), // 是否启用
                true, true, true, // 账号不过期、凭证不过期、未锁定 (暂时默认 true)
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole())) // 权限
        );
    }
}
