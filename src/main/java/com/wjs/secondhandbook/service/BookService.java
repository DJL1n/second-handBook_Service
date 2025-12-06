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

    // 1. 获取市场列表 (支持搜索)
    public Iterable<Book> getMarketplaceBooks(String keyword) {
        if (keyword != null && !keyword.isEmpty()) {
            return bookRepository.searchOnSale(keyword);
        }
        return bookRepository.findByStatus("ON_SALE");
    }

    // 2. 获取“我”发布的书
    public Iterable<Book> getMyBooks() {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户未找到"));
        return bookRepository.findBySellerId(user.getUserId());
    }

    // 3. 购买书籍 (简单的逻辑：把状态改为 SOLD)
    public void buyBook(Integer bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("书籍不存在"));

        if (!"ON_SALE".equals(book.getStatus())) {
            throw new RuntimeException("手慢了！这本书已经卖出去了。");
        }

        // 这里的逻辑比较简单，实际项目中还要判断“不能买自己的书”等
        book.setStatus("SOLD");
        bookRepository.save(book);
    }

    // 辅助方法：获取当前登录用户名
    private String getCurrentUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        return principal.toString();
    }
}
