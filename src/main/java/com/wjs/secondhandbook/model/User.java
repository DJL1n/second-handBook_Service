package com.wjs.secondhandbook.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Table("users")
public class User {
    @Id
    private Integer userId;

    private String username;
    private String password;
    private Boolean enabled;

    // 这个 role 只用于 Security 后台鉴权 (ADMIN/USER)，不用于业务逻辑
    private String role;

    private LocalDateTime lastActiveAt;

    // 增加一个辅助方法，供前端判断
    public boolean isOnline() {
        if (lastActiveAt == null) return false;
        // 如果最后活跃时间在 5 分钟内，返回 true
        return lastActiveAt.isAfter(LocalDateTime.now().minusMinutes(3));
    }
}
