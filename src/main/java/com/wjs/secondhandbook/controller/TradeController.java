package com.wjs.secondhandbook.controller;

import com.wjs.secondhandbook.model.User;
import com.wjs.secondhandbook.repository.UserRepository;
import com.wjs.secondhandbook.service.TradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trade")
public class TradeController {

    @Autowired private TradeService tradeService;
    @Autowired private UserRepository userRepository;

    // 辅助方法：获取当前登录用户的 ID
    private Integer getCurrentUserId(Authentication auth) {
        if (auth == null) {
            throw new RuntimeException("用户未登录");
        }
        String username = auth.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("当前用户不存在"));
        return user.getUserId();
    }

    // 1. 下单购买
    @PostMapping("/buy/{productId}")
    public String buyProduct(@PathVariable Integer productId, Authentication auth) {
        try {
            Integer buyerId = getCurrentUserId(auth);
            tradeService.createOrder(buyerId, productId);
            // 注意：这里返回的是 JSON 字符串
            return "{\"success\": true, \"message\": \"下单成功！去'我的订单'看看吧\"}";
        } catch (Exception e) {
            e.printStackTrace(); // 在控制台打印报错，方便调试
            // 失败时 success: false
            return "{\"success\": false, \"message\": \"" + e.getMessage() + "\"}";
        }
    }

    // 2. 卖家发货
    @PostMapping("/ship/{orderId}")
    public String shipOrder(@PathVariable Integer orderId, Authentication auth) {
        try {
            Integer sellerId = getCurrentUserId(auth);
            tradeService.shipOrder(sellerId, orderId);
            return "{\"success\": true, \"message\": \"发货成功！\"}";
        } catch (Exception e) {
            return "{\"success\": false, \"message\": \"" + e.getMessage() + "\"}";
        }
    }

    // 3. 买家确认收货
    @PostMapping("/receive/{orderId}")
    public String confirmReceive(@PathVariable Integer orderId, Authentication auth) {
        try {
            Integer buyerId = getCurrentUserId(auth);
            tradeService.confirmReceipt(buyerId, orderId);
            return "{\"success\": true, \"message\": \"交易完成！\"}";
        } catch (Exception e) {
            return "{\"success\": false, \"message\": \"" + e.getMessage() + "\"}";
        }
    }
}
