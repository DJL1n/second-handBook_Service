package com.wjs.secondhandbook.controller;

import com.wjs.secondhandbook.model.Product;
import com.wjs.secondhandbook.model.User;
import com.wjs.secondhandbook.repository.OrderRepository;
import com.wjs.secondhandbook.repository.ProductRepository;
import com.wjs.secondhandbook.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class WebController {

    @Autowired private ProductRepository productRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private UserRepository userRepository;

    // 1. 首页：闲鱼风市场 (默认只查 ON_SALE 的书)
    @GetMapping("/")
    public String index(Model model, @RequestParam(value = "keyword", required = false) String keyword) {
        List<Product> products;

        // 1. 先查出商品列表
        if (keyword != null && !keyword.isEmpty()) {
            products = productRepository.findByTitleContainingAndStatus(keyword, "ON_SALE");
        } else {
            products = productRepository.findByStatus("ON_SALE");
        }

        // 2. 🔥【关键步骤】遍历列表，填入卖家名字
        for (Product p : products) {
            // 根据 sellerId 去用户表查 User
            userRepository.findById(p.getSellerId()).ifPresent(user -> {
                // 把查到的用户名填入 product 对象
                p.setSellerName(user.getUsername());
            });
        }

        model.addAttribute("products", products);
        model.addAttribute("keyword", keyword);
        return "index"; // 这里记得改成你实际的 HTML 文件名，你上面代码写的是 market_v2，如果是那个就填 market_v2
    }

    // 2. 商品详情页
    @GetMapping("/product/{id}")
    public String productDetail(@PathVariable Integer id, Model model) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("商品未找到"));

        // 🔥【关键步骤】详情页也要填名字
        userRepository.findById(product.getSellerId()).ifPresent(user -> {
            product.setSellerName(user.getUsername());
        });

        model.addAttribute("product", product);
        return "product_detail";
    }

    // 3. 个人中心：查看我的订单 (买到的 + 卖出的)
    @GetMapping("/my-profile")
    public String myProfile(Model model, Authentication auth) {
        // 获取当前登录用户 ID (使用 Optional 解包)
        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("用户未找到"));
        Integer userId = user.getUserId();

        // 我买到的订单
        model.addAttribute("buyOrders", orderRepository.findByBuyerId(userId));

        // 我卖出的订单
        model.addAttribute("sellOrders", orderRepository.findBySellerId(userId));

        return "profile"; // 对应 templates/profile.html
    }

    // 4. 我的书架：管理我发布的商品
    @GetMapping("/my-shelf")
    public String myShelf(Model model, Authentication auth) {
        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("用户未找到"));

        // 查我发布的所有书 (包括已卖出的)
        model.addAttribute("products", productRepository.findBySellerId(user.getUserId()));

        return "my_shelf"; // 对应 templates/my_shelf.html
    }
}
