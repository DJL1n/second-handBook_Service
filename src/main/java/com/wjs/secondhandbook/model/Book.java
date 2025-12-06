package com.wjs.secondhandbook.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Table("books")
public class Book {
    @Id
    private Integer bookId;

    private Integer sellerId; // 卖家的用户ID

    private String title;     // 书名

    private String author;    // 作者

    private String description; // 描述

    private BigDecimal price;   // 价格

    private String status;      // ON_SALE (在售), SOLD (已售)

    private LocalDateTime createdAt;
}
