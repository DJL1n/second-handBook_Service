package com.wjs.secondhandbook.repository;

import com.wjs.secondhandbook.model.Book;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends CrudRepository<Book, Integer> {
    // 查询某个卖家发布的所有书
    List<Book> findBySellerId(Integer sellerId);

    // 查询所有在售的书
    List<Book> findByStatus(String status);
}
