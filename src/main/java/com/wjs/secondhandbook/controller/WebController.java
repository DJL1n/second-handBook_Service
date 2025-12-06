package com.wjs.secondhandbook.controller;

import com.wjs.secondhandbook.model.Product;
import com.wjs.secondhandbook.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class WebController {

    @Autowired
    private ProductRepository productRepository;

    // 1. 首页：闲鱼风市场
    @GetMapping("/")
    public String index(Model model) {
        // 只查在售的
        model.addAttribute("products", productRepository.findByStatus("ON_SALE"));
        return "market_v2"; // 我们做一个新页面
    }

    // 2. 商品详情页
    @GetMapping("/product/{id}")
    public String productDetail(@PathVariable Integer id, Model model) {
        Product product = productRepository.findById(id).orElseThrow();
        model.addAttribute("product", product);
        return "product_detail";
    }
}
