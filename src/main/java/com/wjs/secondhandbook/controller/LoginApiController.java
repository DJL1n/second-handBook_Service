package com.wjs.secondhandbook.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class LoginApiController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/api/login")
    public Map<String, Object> login(@RequestParam String username,
                                     @RequestParam String password,
                                     HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 1. 把前端传来的账号密码封装成 Token
            UsernamePasswordAuthenticationToken token =
                    new UsernamePasswordAuthenticationToken(username, password);

            // 2. 调用 AuthenticationManager 进行验证
            // 它会自动去 UserDetailsServiceImpl 查库，并比对明文密码
            Authentication authentication = authenticationManager.authenticate(token);

            // 3. 验证通过！手动将登录状态保存到 SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 4. 手动保存到 Session (这就叫“真登录”)
            HttpSession session = request.getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());

            result.put("success", true);
            result.put("message", "登录成功");

        } catch (AuthenticationException e) {
            // 验证失败 (密码错或账号不存在)
            result.put("success", false);
            result.put("message", "账号或密码错误");
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "系统繁忙");
        }

        return result;
    }
}
