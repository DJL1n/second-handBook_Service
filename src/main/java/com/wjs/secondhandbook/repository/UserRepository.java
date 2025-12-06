package com.wjs.secondhandbook.repository;

import com.wjs.secondhandbook.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
    // 自动生成 SQL: SELECT * FROM users WHERE username = ?
    Optional<User> findByUsername(String username);
}
