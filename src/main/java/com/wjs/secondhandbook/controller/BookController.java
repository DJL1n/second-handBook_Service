package com.wjs.secondhandbook.controller;

import com.wjs.secondhandbook.model.Book;
import com.wjs.secondhandbook.service.BookService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
}
