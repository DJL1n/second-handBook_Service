package com.wjs.secondhandbook.controller;

import com.wjs.secondhandbook.model.User;
import com.wjs.secondhandbook.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController // 注意这里用 RestController，直接返回数据而不是页面，方便 API 测试
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public String register(@RequestBody User user) {
        try {
            userService.register(user);
            return "注册成功！用户名：" + user.getUsername();
        } catch (Exception e) {
            return "注册失败：" + e.getMessage();
        }
    }
}
