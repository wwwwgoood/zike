package com.example.demo.tool;

import com.example.demo.config.*;
import io.jsonwebtoken.*;// JWT核心工具类（生成、解析令牌等）
import io.jsonwebtoken.security.Keys;// JWT密钥生成工具（用于HS系列算法）

import org.springframework.stereotype.Component;// 标识Spring组件

import javax.crypto.SecretKey;// 加密密钥接口（JWT签名需要）
import java.util.Date;// 日期类（用于设置令牌签发/过期时间）
import java.util.Map;// 键值对集合（用于存储JWT自定义声明）

@Component
public class JwtUtils {

    private final JwtConfig jwtConfig;

    // 告诉Spring通过构造器注入JwtConfig实例
    public JwtUtils(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig; // 初始化成员变量
    }

    private SecretKey getSecretKey() {
        // 校验密钥长度（HS256算法要求至少256位=32字节）
        if (jwtConfig.getSecret().getBytes().length < 32) {
            throw new IllegalArgumentException("JWT secret must be at least 32 bytes long");
        }
        // 基于配置的secret生成HS256算法所需的密钥
        return Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes());
    }

    public String generateAccessToken(String subject, Map<String, Object> claims) {
        return generateToken(subject, claims, jwtConfig.getAccessTokenExpire());
    }

    /**
     * 生成RefreshToken
     */
    public String generateRefreshToken(String subject) {
        return generateToken(subject, null, jwtConfig.getRefreshTokenExpire());
    }

    /**
     * 通用生成Token方法
     */
    private String generateToken(String subject, Map<String, Object> claims, long expireMillis) {
        Date now = new Date(); // 当前时间（作为令牌签发时间）
        Date expiration = new Date(now.getTime() + expireMillis); // 计算过期时间（当前时间+过期毫秒数）

        // 构建JWT令牌
        JwtBuilder builder = Jwts.builder()
                .setIssuer(jwtConfig.getIssuer()) // 设置签发人（如系统名称，可选）
                .setSubject(subject) // 设置主题（用户唯一标识）
                .setIssuedAt(now) // 设置签发时间
                .setExpiration(expiration) // 设置过期时间
                .signWith(getSecretKey(), SignatureAlgorithm.HS256); // 设置签名密钥和算法

        // 如果有自定义声明，添加到令牌中
        if (claims != null && !claims.isEmpty()) {
            builder.setClaims(claims);//setClaims：添加自定义声明（如用户角色role: "admin"），这些信息会明文存储在 payload 中（注意不要放敏感信息）。
        }

        // 生成紧凑的JWT字符串（三部分：header.payload.signature）
        return builder.compact();//compact()：将构建器配置的内容转换为最终的 JWT 字符串（格式：xxx.yyy.zzz）。
    }

    /**
     * 解析Token，获取负载信息
     * 
     * @param token Token字符串
     * @return 负载Claims
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSecretKey()) // 验证密钥
                    .build()
                    .parseClaimsJws(token) // 解析Token
                    .getBody();
        } catch (JwtException | IllegalArgumentException e) {
            // Token无效（过期、签名错误等）
            throw new RuntimeException("Invalid token: " + e.getMessage());
        }
    }

    /**
     * 验证Token是否有效
     * 
     * @param token Token字符串
     * @return true=有效
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token); // 解析成功即有效
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}