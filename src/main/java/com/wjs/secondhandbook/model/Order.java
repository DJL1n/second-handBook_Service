package com.wjs.secondhandbook.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Table("orders")
public class Order {
    @Id
    private Integer id;
    private Integer productId;
    private Integer buyerId;
    private Integer sellerId;
    private BigDecimal price;
    private String status; // WAIT_SHIP, WAIT_RECEIVE, COMPLETED, REFUNDING, REFUNDED
    private String shippingAddress;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
