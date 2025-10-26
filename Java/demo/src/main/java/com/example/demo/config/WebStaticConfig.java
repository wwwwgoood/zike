package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.lang.NonNull;

@Configuration
public class WebStaticConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        // 配置：访问路径前缀 "/res/**" 对应 项目目录 "src/main/resources/res/"
        registry.addResourceHandler("/res/**") // 前端访问时的URL前缀
                .addResourceLocations("classpath:/res/"); // 资源在项目中的实际路径
    }
}
