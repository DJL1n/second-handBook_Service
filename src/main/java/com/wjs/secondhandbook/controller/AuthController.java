package com.wjs.secondhandbook.controller;

import com.wjs.secondhandbook.model.User;
import com.wjs.secondhandbook.service.UserService;
import jakarta.servlet.http.HttpServletResponse; // 记得导入这个包
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // 1. 返回类型改为 void (因为我们要自己控制响应)
    // 2. 增加参数 HttpServletResponse response
    @PostMapping("/register")
    public void register(User user, HttpServletResponse response) throws IOException {
        try {
            userService.register(user);

            // ✅ 成功的情况：直接命令浏览器跳转回首页
            response.sendRedirect("/");

        } catch (Exception e) {
            // ❌ 失败的情况（比如用户名重复）：
            // 简单处理：返回一段 HTML 告诉用户错了，并给一个返回按钮
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().write("<script>alert('注册失败：" + e.getMessage() + "'); window.history.back();</script>");
        }
    }
}
