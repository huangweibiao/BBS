package com.bbs.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT配置属性类
 * 用于配置JWT相关参数
 */
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    
    /** JWT密钥 */
    private String secret;
    
    /** 过期时间（毫秒），默认24小时 */
    private Long expiration = 86400000L;
    
    /** 请求头名称 */
    private String headerName = "Authorization";
    
    /** Token前缀 */
    private String tokenPrefix = "Bearer ";
    
    public String getSecret() {
        return secret;
    }
    
    public void setSecret(String secret) {
        this.secret = secret;
    }
    
    public Long getExpiration() {
        return expiration;
    }
    
    public void setExpiration(Long expiration) {
        this.expiration = expiration;
    }
    
    public String getHeaderName() {
        return headerName;
    }
    
    public void setHeaderName(String headerName) {
        this.headerName = headerName;
    }
    
    public String getTokenPrefix() {
        return tokenPrefix;
    }
    
    public void setTokenPrefix(String tokenPrefix) {
        this.tokenPrefix = tokenPrefix;
    }
}