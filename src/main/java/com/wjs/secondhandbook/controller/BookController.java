package com.wjs.secondhandbook.controller;

import com.wjs.secondhandbook.model.Book;
import com.wjs.secondhandbook.service.BookService;
import org.springframework.web.bind.annotation.*;

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

    // 1. 浏览市场：GET /books?keyword=Java
    @GetMapping("/books")
    public Iterable<Book> getAllBooks(@RequestParam(value = "keyword", required = false) String keyword) {
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
