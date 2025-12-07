package com.wjs.secondhandbook.service;

import com.wjs.secondhandbook.model.Order;
import com.wjs.secondhandbook.model.Product;
import com.wjs.secondhandbook.repository.OrderRepository;
import com.wjs.secondhandbook.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class TradeService {

    @Autowired private ProductRepository productRepository;
    @Autowired private OrderRepository orderRepository;

    /**
     * 1. 买家下单
     */
    @Transactional // 事务控制：要么全成功，要么全失败
    public void createOrder(Integer buyerId, Integer productId) {
        // 1. 查商品
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("商品不存在"));

        // 2. 检查状态
        if (!"ON_SALE".equals(product.getStatus())) {
            throw new RuntimeException("手慢了，商品已被抢走！");
        }

        // 3. 不能买自己的书
        if (product.getSellerId().equals(buyerId)) {
            throw new RuntimeException("不能购买自己发布的商品");
        }

        // 4. 更新商品状态 -> 已售出
        product.setStatus("SOLD");
        productRepository.save(product);

        // 5. 创建订单
        Order order = new Order();
        order.setProductId(productId);
        order.setBuyerId(buyerId);
        order.setSellerId(product.getSellerId());
        order.setPrice(product.getPrice());
        order.setStatus("WAIT_SHIP"); // 初始状态：待发货
        order.setShippingAddress("学校图书馆大厅"); // 暂时写死，后续可扩展

        // 🔥 手动设置时间，防止数据库报错
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        orderRepository.save(order);
    }

    /**
     * 2. 卖家发货
     */
    @Transactional
    public void shipOrder(Integer sellerId, Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        if (!order.getSellerId().equals(sellerId)) {
            throw new RuntimeException("您无权操作此订单");
        }

        if ("WAIT_SHIP".equals(order.getStatus())) {
            order.setStatus("WAIT_RECEIVE");
            order.setUpdatedAt(LocalDateTime.now()); // 更新时间
            orderRepository.save(order);
        }
    }

    /**
     * 3. 买家确认收货
     */
    @Transactional
    public void confirmReceipt(Integer buyerId, Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        if (!order.getBuyerId().equals(buyerId)) {
            throw new RuntimeException("您无权操作此订单");
        }

        if ("WAIT_RECEIVE".equals(order.getStatus())) {
            order.setStatus("COMPLETED");
            order.setUpdatedAt(LocalDateTime.now()); // 更新时间
            orderRepository.save(order);
        }
    }
}
