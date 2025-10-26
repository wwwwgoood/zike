package com.example.demo.repository;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;//引入optional

// 泛型参数：<实体类, 主键类型>
public interface UserRepository extends JpaRepository<User, Long> {
    // 无需写任何方法！JpaRepository已自带save()、findAll()等方法
    Optional<User> findByName(String username);
}