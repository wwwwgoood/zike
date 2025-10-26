package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.Data;

@Data
@Entity  // 标记为 JPA 实体（对应数据库表）
public class User {
    @Id  // 主键
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 自增主键
    private Long id;

    private String name;
    private String password;
    private String token;

    // 必须有默认构造函数（JPA 要求）
    public User() {}

}