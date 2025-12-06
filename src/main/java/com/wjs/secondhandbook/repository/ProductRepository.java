package com.wjs.secondhandbook.repository;

import com.wjs.secondhandbook.model.Product;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends CrudRepository<Product, Integer> {
    List<Product> findByStatus(String status);
}
