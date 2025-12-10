package com.wjs.secondhandbook.controller;

import com.wjs.secondhandbook.model.Order;
import com.wjs.secondhandbook.model.Product;
import com.wjs.secondhandbook.model.User;
import com.wjs.secondhandbook.repository.OrderRepository;
import com.wjs.secondhandbook.repository.ProductRepository;
import com.wjs.secondhandbook.repository.UserRepository;
import com.wjs.secondhandbook.service.TradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/trade")
public class TradeController {

    @Autowired private TradeService tradeService;
    @Autowired private UserRepository userRepository;

    // 为了实现取消订单逻辑，直接注入 Repository
    @Autowired private OrderRepository orderRepository;
    @Autowired private ProductRepository productRepository;

    // --- 辅助方法：生成统一的成功响应 ---
    private Map<String, Object> success(String message) {
        Map<String, Object> map = new HashMap<>();
        map.put("success", true);
        map.put("code", 200);
        map.put("message", message);
        return map;
    }

    // --- 辅助方法：生成统一的失败响应 ---
    private Map<String, Object> error(String message) {
        Map<String, Object> map = new HashMap<>();
        map.put("success", false);
        map.put("code", 400);
        map.put("message", message);
        return map;
    }

    // --- 辅助方法：获取当前登录用户的 ID ---
    private Integer getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("用户未登录");
        }
        String username = auth.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("当前用户不存在"));

        // 修正 1: 这里改回 getUserId()，因为你的 User 实体类字段应该是 userId
        return user.getUserId();
    }


    // 1. 下单购买
    @PostMapping("/buy/{productId}")
    public Map<String, Object> buyProduct(@PathVariable Integer productId) {
        try {
            Integer buyerId = getCurrentUserId();
            tradeService.createOrder(buyerId, productId);
            return success("下单成功！去'个人中心'看看吧");
        } catch (Exception e) {
            e.printStackTrace();
            return error(e.getMessage());
        }
    }

    // 2. 卖家发货
    @PostMapping("/ship/{orderId}")
    public Map<String, Object> shipOrder(@PathVariable Integer orderId) {
        try {
            Integer sellerId = getCurrentUserId();
            tradeService.shipOrder(sellerId, orderId);
            return success("发货成功！");
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    // 3. 买家确认收货
    @PostMapping("/receive/{orderId}")
    public Map<String, Object> confirmReceive(@PathVariable Integer orderId) {
        try {
            Integer buyerId = getCurrentUserId();
            tradeService.confirmReceipt(buyerId, orderId);
            return success("交易完成！");
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    /**
     * 4. 卖家取消订单（拒绝发货）
     */
    @PostMapping("/cancel/{orderId}")
    public Map<String, Object> cancelOrder(@PathVariable Integer orderId) {
        try {
            // 1. 获取当前用户
            Integer currentUserId = getCurrentUserId();

            // 2. 查订单
            // 修正 2: 去掉 Long.valueOf，直接传 Integer
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("订单不存在"));

            // 3. 安全检查
            // 修正 3: 这里比较 Integer，无需 Long 转换
            // 这里假设 getSellerId() 返回也是 Integer
            if (!order.getSellerId().equals(currentUserId)) {
                return error("你无权操作此订单");
            }
            if (!"WAIT_SHIP".equals(order.getStatus())) {
                return error("当前状态不可取消");
            }

            // 4. 执行取消逻辑
            order.setStatus("CANCELLED");
            order.setUpdatedAt(LocalDateTime.now());
            orderRepository.save(order);

            // 5. 商品重新上架
            // 修正 4: 去掉 Long.valueOf，直接传 Integer
            Product product = productRepository.findById(order.getProductId())
                    .orElseThrow(() -> new RuntimeException("关联商品不存在"));

            product.setStatus("ON_SALE");
            productRepository.save(product);

            return success("订单已取消，商品已重新上架！");

        } catch (Exception e) {
            e.printStackTrace();
            return error("系统错误: " + e.getMessage());
        }
    }

    @PostMapping("/return/{id}")
    @ResponseBody
    public Map<String, Object> requestReturn(@PathVariable Integer id, @RequestBody Map<String, String> payload, Authentication auth) {
        User me = userRepository.findByUsername(auth.getName()).orElseThrow();
        Order order = orderRepository.findById(id).orElseThrow();

        if (!order.getBuyerId().equals(me.getUserId())) {
            return Map.of("success", false, "message", "无权操作");
        }

        // 只有“待收货”状态才能申请退货
        if (!"WAIT_RECEIVE".equals(order.getStatus())) {
            return Map.of("success", false, "message", "当前状态无法申请退货");
        }

        // 1. 修改状态为退货申请中
        order.setStatus("RETURN_REQUESTED");
        // 2. 写入理由
        order.setReturnReason(payload.get("reason"));
        orderRepository.save(order);

        return Map.of("success", true, "message", "退货申请已提交，等待管理员审核");
    }

}
