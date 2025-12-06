package com.wjs.secondhandbook.service;

import com.wjs.secondhandbook.model.Book;
import com.wjs.secondhandbook.model.User;
import com.wjs.secondhandbook.repository.BookRepository;
import com.wjs.secondhandbook.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public BookService(BookRepository bookRepository, UserRepository userRepository) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    public Book addBook(Book book) {
        // 1. 获取当前登录用户的用户名
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        // 2. 根据用户名查用户ID (为了关联数据)
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户未找到"));

        // 3. 填充书籍信息
        book.setSellerId(user.getUserId());
        book.setStatus("ON_SALE");
        book.setCreatedAt(LocalDateTime.now());

        return bookRepository.save(book);
    }
}
