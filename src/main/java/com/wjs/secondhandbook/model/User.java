package com.wjs.secondhandbook.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

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
}
