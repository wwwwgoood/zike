package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import com.example.demo.tool.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.io.IOException;
import java.util.Collections;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:8080") // 替换为你的前端实际地址
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private MediaResourceProcessor mediaResourceProcessor;

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody User user) {
        Map<String, Object> resMap = new HashMap<String, Object>();

        if (userService.FindExistByName(user.getName())) {
            resMap.put("descrpiton", "cnm的，以及有了还往里塞");
            return resMap;
        }
        resMap.put("descrpition", "注册成功！用户信息已保存到数据库");

        String token = jwtUtils.generateAccessToken("login", null);
        user.setToken(token);
        resMap.put("token", token);
        userService.savedUser(user);
        System.out.println(resMap);
        return resMap;
    }

    @PostMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/songs")
    public List<Map<String, Object>> getAllSongs() {
        try {
            // 处理src/main/resources目录下的media文件夹（根据你的实际目录修改）
            return mediaResourceProcessor.processMediaFiles("static/res"); // 直接返回处理结果
        } catch (IOException e) {
            e.printStackTrace();
            // 异常情况下返回空列表，或根据需求抛出RuntimeException让Spring捕获
            return Collections.emptyList(); 
            // 另一种方案：抛出异常（推荐，便于前端接收错误状态）
            // throw new RuntimeException("获取歌曲列表失败", e);
        }
    }

}