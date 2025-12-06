package com.wjs.secondhandbook.controller;

import com.wjs.secondhandbook.model.Book;
import com.wjs.secondhandbook.service.BookService;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Enumeration;

@RestController
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping("/books")
    public String createBook(@RequestBody Book book) {
        try {
            Book savedBook = bookService.addBook(book);
            return "发布成功！书籍ID：" + savedBook.getBookId();
        } catch (Exception e) {
            return "发布失败：" + e.getMessage();
        }
    }

    @GetMapping("/books")
    public Iterable<Book> getAllBooks(
            @RequestParam(value = "keyword", required = false) String keyword,
            HttpServletRequest request // <--- 1. 增加这个参数
    ) {
        // --- 🕵️‍♂️ 侦探代码开始 ---
        System.out.println("========== 收到 /books 请求 ==========");
        System.out.println("Cookie 头信息: " + request.getHeader("Cookie"));

        System.out.println("--- 所有 Header 清单 ---");
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            String value = request.getHeader(key);
            System.out.println(key + ": " + value);
        }
        System.out.println("======================================");
        // --- 侦探代码结束 ---

        return bookService.getMarketplaceBooks(keyword);
    }

    // 2. 我的书架：GET /books/my
    @GetMapping("/books/my")
    public Iterable<Book> getMyBooks() {
        return bookService.getMyBooks();
    }

    // 3. 购买书籍：POST /books/buy/1
    @PostMapping("/books/buy/{bookId}")
    public String buyBook(@PathVariable Integer bookId) {
        try {
            bookService.buyBook(bookId);
            return "购买成功！书已下架。";
        } catch (Exception e) {
            return "购买失败：" + e.getMessage();
        }
    }
}
