package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import org.springframework.lang.NonNull;

@Configuration  // 标识为配置类
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping("/**")  // 对所有接口生效
                .allowedOrigins("http://localhost:8080/","http://127.0.0.1:5500")  // 允许前端源访问（你的前端地址）
                .allowedMethods("GET", "POST", "PUT", "DELETE")  // 允许的请求方法
                .allowedHeaders("*")  // 允许所有请求头
                .allowCredentials(true)  // 允许携带Cookie（如果需要）
                .maxAge(3600);  // 预检请求的有效期（秒），避免频繁预检
    }
}