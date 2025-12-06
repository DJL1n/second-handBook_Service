package com.wjs.secondhandbook.service;

import com.wjs.secondhandbook.model.Order;
import com.wjs.secondhandbook.model.Product;
import com.wjs.secondhandbook.repository.OrderRepository;
import com.wjs.secondhandbook.repository.ProductRepository;
import com.wjs.secondhandbook.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TradeService {

    @Autowired private ProductRepository productRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private UserRepository userRepository; // 如果需要查用户详情

    /**
     * 1. 买家下单
     */
    @Transactional
    public void createOrder(Integer buyerId, Integer productId) {
        // 1. 查商品
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("商品不存在"));

        // 2. 检查状态
        if (!"ON_SALE".equals(product.getStatus())) {
            throw new RuntimeException("商品已被抢走啦！");
        }

        // 3. 不能买自己的书
        if (product.getSellerId().equals(buyerId)) {
            throw new RuntimeException("不能买自己的书哦");
        }

        // 4. 更新商品状态 -> 已售出 (防止超卖)
        product.setStatus("SOLD");
        productRepository.save(product);

        // 5. 创建订单
        Order order = new Order();
        order.setProductId(productId);
        order.setBuyerId(buyerId);
        order.setSellerId(product.getSellerId());
        order.setPrice(product.getPrice());
        order.setStatus("WAIT_SHIP"); // 初始状态：待发货
        order.setShippingAddress("学校图书馆大厅"); // 暂时写死
        orderRepository.save(order);
    }

    /**
     * 2. 卖家发货
     */
    @Transactional
    public void shipOrder(Integer sellerId, Integer orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow();

        // 只有卖家本人能操作
        if (!order.getSellerId().equals(sellerId)) {
            throw new RuntimeException("无权操作");
        }

        if ("WAIT_SHIP".equals(order.getStatus())) {
            order.setStatus("WAIT_RECEIVE");
            orderRepository.save(order);
        }
    }

    /**
     * 3. 买家确认收货
     */
    @Transactional
    public void confirmReceipt(Integer buyerId, Integer orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow();

        if (!order.getBuyerId().equals(buyerId)) {
            throw new RuntimeException("无权操作");
        }

        if ("WAIT_RECEIVE".equals(order.getStatus())) {
            order.setStatus("COMPLETED");
            orderRepository.save(order);
        }
    }

    // ... 后续还可以加 refundOrder (申请退款)
}
