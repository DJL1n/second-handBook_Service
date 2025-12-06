package com.wjs.secondhandbook.controller;

import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController // 注意这里用 RestController 或者在方法上加 @ResponseBody
public class LoginApiController {

    @PostMapping("/api/login")
    public Map<String, Object> login(@RequestParam String username, @RequestParam String password) {
        Map<String, Object> result = new HashMap<>();

        // --- 模拟你的登录逻辑 ---
        // 假设账号是 admin，密码是 123456
        if ("admin".equals(username) && "123456".equals(password)) {
            result.put("success", true);
            result.put("message", "登录成功");
            // 这里可以处理 Session 写入
        } else {
            result.put("success", false);
            result.put("message", "账号或密码错误！");
        }

        return result; // 返回给前端的是 JSON 数据
    }
}
