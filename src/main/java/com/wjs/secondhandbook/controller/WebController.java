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

@Controller
public class WebController {

    @Autowired private ProductRepository productRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private UserRepository userRepository;

    // 1. 首页：闲鱼风市场 (默认只查 ON_SALE 的书)
    @GetMapping("/")
    public String index(Model model, @RequestParam(value = "keyword", required = false) String keyword) {
        if (keyword != null && !keyword.isEmpty()) {
            // 搜索功能：按标题搜索且必须是在售状态
            model.addAttribute("products", productRepository.findByTitleContainingAndStatus(keyword, "ON_SALE"));
        } else {
            // 默认展示：查所有在售商品
            model.addAttribute("products", productRepository.findByStatus("ON_SALE"));
        }
        model.addAttribute("keyword", keyword); // 回显搜索词
        return "market_v2"; // 对应 templates/market_v2.html
    }

    // 2. 商品详情页
    @GetMapping("/product/{id}")
    public String productDetail(@PathVariable Integer id, Model model) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("商品未找到"));
        model.addAttribute("product", product);
        return "product_detail"; // 对应 templates/product_detail.html
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
