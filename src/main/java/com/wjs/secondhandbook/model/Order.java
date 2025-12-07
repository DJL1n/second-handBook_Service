package com.wjs.secondhandbook.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("orders") // 🔥 必须指定表名为 orders，避开 SQL 关键字 order
public class Order {

    @Id // ⚠️ 必须是 org.springframework.data.annotation.Id
    private Integer id;

    private Integer productId;
    private Integer buyerId;
    private Integer sellerId;
    private BigDecimal price;
    private String status;
    private String shippingAddress;

    // 建议在 Java 层控制时间，比依赖数据库默认值更稳妥
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
