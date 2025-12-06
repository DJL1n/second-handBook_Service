package com.wjs.secondhandbook.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import java.math.BigDecimal;
import lombok.Data; // 记得装 Lombok 插件，或者手动写 Getters/Setters

@Data
@Table("products")
public class Product {
    @Id
    private Integer productId;
    private Integer sellerId;
    private Integer categoryId;
    private String title;
    private String description;
    private BigDecimal price;
    private String imageUrl; // 数据库里存的是 "/images/xxx.jpg"
    private String status;   // ON_SALE, SOLD
}
