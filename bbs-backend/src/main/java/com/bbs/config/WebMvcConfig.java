package com.bbs.config;

import com.bbs.interceptor.JwtInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC配置类
 * 用于注册拦截器、静态资源等
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    @Resource
    private JwtInterceptor jwtInterceptor;
    
    /**
     * 注册拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/**")  // 拦截所有API请求
                .excludePathPatterns(         // 排除不需要拦截的路径
                        "/api/register",
                        "/api/login",
                        "/api/forums",
                        "/api/topics/hot"
                );
    }
}