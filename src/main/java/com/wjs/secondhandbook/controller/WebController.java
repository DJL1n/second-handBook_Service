package com.wjs.secondhandbook.controller;

import com.wjs.secondhandbook.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller; // 注意：这里用 @Controller，不是 @RestController
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller // 说明这个类返回的是 HTML 页面，而不是 JSON 数据
public class WebController {

    @Autowired
    private BookService bookService;

    // 1. 首页：二手书市场
    @GetMapping("/")
    public String index(Model model, @RequestParam(value = "keyword", required = false) String keyword) {
        // 把查询到的书籍列表放入 model 中，传给 HTML
        model.addAttribute("books", bookService.getMarketplaceBooks(keyword));
        model.addAttribute("keyword", keyword); // 把搜索词也传回去，回显在输入框里
        return "market"; // 对应 src/main/resources/templates/market.html
    }

    // 2. 页面：我的书架
    @GetMapping("/my-shelf")
    public String myShelf(Model model) {
        model.addAttribute("books", bookService.getMyBooks());
        return "my_shelf"; // 对应 src/main/resources/templates/my_shelf.html
    }

    // 3. 页面：登录页 (Spring Security 默认有个丑丑的，我们先用它，或者自定义)
    // 暂时先不用写，直接用 Spring Security 自带的
}
