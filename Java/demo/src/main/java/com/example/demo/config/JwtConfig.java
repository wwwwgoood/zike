package com.example.demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;//导入 Spring Boot 的ConfigurationProperties注解，用于将配置文件中的属性与当前类的字段进行绑定。
import org.springframework.context.annotation.Configuration;//导入 Spring 的Configuration注解，标识当前类是一个配置类，会被 Spring 容器扫描并纳入管理（作为 Bean 存在）。

/**
 * JWT配置类，通过@ConfigurationProperties绑定application.properties中的jwt前缀配置
 */
@Configuration
@ConfigurationProperties(prefix = "jwt") // 绑定配置文件中"jwt."开头的属性
public class JwtConfig {

    /*
     * 在 JWT 认证流程中，通常会有两种令牌：访问令牌（AccessToken） 和刷新令牌（RefreshToken）。
     * AccessToken：是客户端（如前端）访问后端受保护接口时必须携带的令牌，用于证明用户身份和权限。为了安全，它的有效期通常很短（比如 1
     * 小时），避免被盗用后造成长期风险。
     * RefreshToken：作用是在 AccessToken 过期后，无需用户重新登录，就能获取新的 AccessToken。它的有效期通常很长（比如 7
     * 天或 30 天）。
     */

    private String secret;// 定义secret字段，用于存储 JWT 的签名密钥，类型为字符串。

    private long accessTokenExpire;// 定义accessTokenExpire字段，存储 AccessToken 的过期时间，类型为长整型（long）。

    private long refreshTokenExpire;// 刷新令牌（RefreshToken）的有效期，单位为毫秒。通常 RefreshToken 的有效期比 AccessToken 长，用于在
                                    // AccessToken 过期后获取新的 AccessToken。

    private String issuer;// 定义issuer字段，存储 JWT 的签发人信息，类型为字符串。

    /*
     * 签发人（Issuer）是 JWT 标准中定义的一个可选声明（Claim），用于标识 “这个 JWT 是谁签发的”（比如一个系统名称、公司域名等）。
     * 它的作用是：让令牌的接收方（比如后端服务）可以验证令牌的来源是否可信。例如：
     */

    // getter和setter（必须提供，否则配置无法绑定）setter 方法：用于接收配置文件中的值。 getter 方法：用于让其他组件获取配置值。
    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getAccessTokenExpire() {
        return accessTokenExpire;
    }

    public void setAccessTokenExpire(long accessTokenExpire) {
        this.accessTokenExpire = accessTokenExpire;
    }

    public long getRefreshTokenExpire() {
        return refreshTokenExpire;
    }

    public void setRefreshTokenExpire(long refreshTokenExpire) {
        this.refreshTokenExpire = refreshTokenExpire;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }
}