package com.wjs.secondhandbook.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * 对应数据库中的 users 表
 */
@Data // Lombok注解：自动生成Getter/Setter/ToString等
@Table("users") // 明确指定映射的数据库表名
public class User {

    @Id // 标记这是主键
    private Integer userId;

    private String username;

    private String password; // 存放加密后的密码

    private String studentId;

    private String role; // ADMIN, SELLER, BUYER

    private String nickname;

    private LocalDateTime createdAt;
}
