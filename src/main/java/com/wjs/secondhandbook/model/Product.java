package com.wjs.secondhandbook.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Table("products")
public class Product {
    @Id
    private Integer id;
    private Integer sellerId;
    private String title;
    private BigDecimal price;
    private String imageUrl;
    private String description;
    private String status;
    private LocalDateTime createdAt;

    @Transient
    private String sellerName;
}
