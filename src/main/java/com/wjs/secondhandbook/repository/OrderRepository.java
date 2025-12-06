package com.wjs.secondhandbook.repository;

import com.wjs.secondhandbook.model.Order;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends CrudRepository<Order, Integer> {
    // 我买到的订单
    List<Order> findByBuyerId(Integer buyerId);

    // 我卖出的订单 (卖家要在这里点发货)
    List<Order> findBySellerId(Integer sellerId);
}
