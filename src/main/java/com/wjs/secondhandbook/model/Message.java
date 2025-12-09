package com.wjs.secondhandbook.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;         // ⚠️ 注意包名
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("messages") // ⚠️ 指定表名
public class Message {

    @Id // ⚠️ org.springframework.data.annotation.Id
    private Integer id;

    // JDBC 默认会自动把驼峰命名 senderId 映射为 sender_id
    // 但为了保险或自定义，你也可以用 @Column
    private Integer senderId;

    private Integer receiverId;

    private Integer productId;

    private String content;

    @Builder.Default
    private Boolean isRead = false; // JDBC 会映射为 TINYINT(1)

    private LocalDateTime createdAt;
}
