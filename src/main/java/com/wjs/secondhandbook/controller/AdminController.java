package com.wjs.secondhandbook.controller;

import com.wjs.secondhandbook.model.*;
import com.wjs.secondhandbook.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired private OrderRepository orderRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private UserRepository userRepository;

    // 🔒 简单的权限检查方法
    private void checkAdmin(Authentication auth) {
        User user = userRepository.findByUsername(auth.getName()).orElseThrow();
        if (!"ADMIN".equals(user.getRole())) {
            throw new RuntimeException("无权访问");
        }
    }

    // 1. 管理员仪表盘页面
    @GetMapping
    public String adminDashboard(Model model, Authentication auth) {
        checkAdmin(auth); // 检查权限

        // A. 查出所有申请退货的订单 (Status = 'RETURN_REQUESTED')
        // 这里假设你的 Repository 是 CrudRepository，需自己根据情况调整查询方式
        // 简单起见，这里查所有然后 filter，实际项目建议写 SQL @Query
        Iterable<Order> allOrders = orderRepository.findAll();
        model.addAttribute("returnOrders",
                java.util.stream.StreamSupport.stream(allOrders.spliterator(), false)
                        .filter(o -> "RETURN_REQUESTED".equals(o.getStatus()))
                        .toList());

        // B. 查出所有在售商品 (方便下架管理)
        model.addAttribute("products", productRepository.findAll());

        return "admin_dashboard";
    }

    // 2. 审核退货 (Action: agree / reject)
    @PostMapping("/audit/{orderId}")
    @ResponseBody
    public Map<String, Object> auditReturn(@PathVariable Integer orderId,
                                           @RequestParam String action,
                                           Authentication auth) {
        checkAdmin(auth);
        Order order = orderRepository.findById(orderId).orElseThrow();

        if ("agree".equals(action)) {
            // 同意：订单设为已退款/已取消
            order.setStatus("REFUNDED");
            // 可选：把商品重新上架？这里简单处理，直接结束交易
            orderRepository.save(order);
            return Map.of("success", true, "message", "已同意退货，订单取消");
        } else {
            // 拒绝：订单回退到“待收货”状态，买家必须收货
            order.setStatus("WAIT_RECEIVE");
            orderRepository.save(order);
            return Map.of("success", true, "message", "已拒绝退货，订单恢复为待收货");
        }
    }

    // 3. 强制下架商品
    @PostMapping("/product/ban/{productId}")
    @ResponseBody
    public Map<String, Object> banProduct(@PathVariable Integer productId, Authentication auth) {
        checkAdmin(auth);
        Product product = productRepository.findById(productId).orElseThrow();

        product.setStatus("BANNED"); // 设为封禁/下架状态
        productRepository.save(product);

        return Map.of("success", true, "message", "商品已强制下架");
    }
}
