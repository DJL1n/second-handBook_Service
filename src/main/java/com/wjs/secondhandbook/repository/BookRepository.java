package com.wjs.secondhandbook.repository;

import com.wjs.secondhandbook.model.Book;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends CrudRepository<Book, Integer> {

    // 1. 找某个卖家的书
    List<Book> findBySellerId(Integer sellerId);

    // 2. 找所有在售的书 (ON_SALE)
    List<Book> findByStatus(String status);

    // 3. 搜索功能：查找标题或作者包含关键词，且状态是 ON_SALE 的书
    // 注意：Spring Data JDBC 使用 @Query 手写 SQL 最稳妥
    @Query("SELECT * FROM books WHERE status = 'ON_SALE' AND (title LIKE concat('%', :keyword, '%') OR author LIKE concat('%', :keyword, '%'))")
    List<Book> searchOnSale(@Param("keyword") String keyword);
}
