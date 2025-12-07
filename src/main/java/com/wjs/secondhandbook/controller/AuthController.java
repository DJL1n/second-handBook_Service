package com.wjs.secondhandbook.controller;

import com.wjs.secondhandbook.model.User;
import com.wjs.secondhandbook.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController; // 注意这里

@RestController
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // 🔥 修改点：去掉了 @RequestBody
    // 这样 Spring 就能自动把表单里的 username 和 password 塞进 User 对象里了
    @PostMapping("/register")
    public String register(User user) {
        try {
            // 注意：虽然前端传了 email，但因为 User 类和数据库没 email 字段，
            // 这里会自动忽略 email，不会报错，只是存不进去而已。
            userService.register(user);

            // 注册成功后，因为这是 RestController，页面会显示这段白底黑字的文字。
            // 以后你可以把它改成重定向到登录页。
            return "注册成功！请返回首页进行登录。";
        } catch (Exception e) {
            return "注册失败：" + e.getMessage();
        }
    }
}
