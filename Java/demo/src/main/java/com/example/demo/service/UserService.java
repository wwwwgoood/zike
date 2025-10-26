package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public void savedUser(User user){
        userRepository.save(user);
    }
    public List<User>getAllUsers() {
        return userRepository.findAll();  // 调用JPA的findAll方法，查询所有记录
    }
    public boolean FindExistByName(String name)
    {
        return userRepository.findByName(name).isPresent();
    }
}