package com.wjs.secondhandbook.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class LoginApiController {

    @Autowired
    private AuthenticationManager authenticationManager;

    // 注入 SecurityConfig 中配置的 Repository
    @Autowired
    private SecurityContextRepository securityContextRepository;

    @PostMapping("/api/login")
    public Map<String, Object> login(@RequestParam String username,
                                     @RequestParam String password,
                                     HttpServletRequest request,
                                     HttpServletResponse response) { // 增加 response 参数
        Map<String, Object> result = new HashMap<>();

        try {
            // 去除可能的空格
            String cleanUsername = username.trim();
            String cleanPassword = password.trim();

            System.out.println("正在尝试登录: " + cleanUsername + " / " + cleanPassword);

            // 1. 封装 Token
            UsernamePasswordAuthenticationToken token =
                    new UsernamePasswordAuthenticationToken(cleanUsername, cleanPassword);

            // 2. 验证 (这里会调用 UserDetailsServiceImpl)
            Authentication authentication = authenticationManager.authenticate(token);

            // 3. 创建并设置 Context
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);

            // 4. 🔥 关键：显式保存 Context 到 Session (Spring Security 6 写法)
            securityContextRepository.saveContext(context, request, response);

            result.put("success", true);
            result.put("message", "登录成功");
            System.out.println("登录成功: " + cleanUsername);

        } catch (AuthenticationException e) {
            // 打印具体错误原因到控制台，方便调试
            System.out.println("登录验证失败: " + e.getMessage());
            result.put("success", false);
            result.put("message", "账号或密码错误");
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "系统错误: " + e.getMessage());
        }

        return result;
    }
}
