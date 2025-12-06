package com.wjs.secondhandbook.repository;

import com.wjs.secondhandbook.model.Product;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends CrudRepository<Product, Integer> {
    // 查市场上的书 (只看在售的)
    List<Product> findByStatus(String status);

    // 查我发布的书 (用户中心用)
    List<Product> findBySellerId(Integer sellerId);

    // 搜索功能 (根据标题模糊查询 且 必须是在售状态)
    List<Product> findByTitleContainingAndStatus(String title, String status);
}
