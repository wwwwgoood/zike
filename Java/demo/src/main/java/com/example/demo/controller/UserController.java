package com.example.demo.controller;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import com.example.demo.tool.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:8080") // 替换为你的前端实际地址
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private FileReturnProcess fileReturnProcess;
   
    @PostMapping("/register")
    public Map<String ,Object> register(@RequestBody User user) {
        Map<String,Object> resMap=new HashMap<String,Object>();
        
        if(userService.FindExistByName(user.getName()))
        {
            resMap.put("descrpiton","cnm的，以及有了还往里塞");
            return resMap;
        }
        resMap.put("descrpition","注册成功！用户信息已保存到数据库");
        
       
        String token=jwtUtils.generateAccessToken("login", null);
        user.setToken(token);
        resMap.put("token",token);
        userService.savedUser(user);
        System.out.println(resMap);
        return resMap;
    }

    @PostMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/songs")
    public List<Map<String ,Object>> getAllSongs() {

        return fileReturnProcess.getAllSongsResource("/res");
    }
    

}