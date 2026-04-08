package com.bbs.interceptor;

import com.bbs.common.JwtUtils;
import com.bbs.common.RedisUtils;
import com.bbs.config.JwtProperties;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.List;

/**
 * JWT认证拦截器
 * 用于验证请求中的JWT Token是否有效
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {
    
    @Resource
    private JwtUtils jwtUtils;
    
    @Resource
    private JwtProperties jwtProperties;
    
    @Resource
    private RedisUtils redisUtils;
    
    /** 不需要拦截的URL */
    private static final List<String> EXCLUDE_PATHS = Arrays.asList(
            "/api/register",
            "/api/login",
            "/api/forums",
            "/api/topics/hot"
    );
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 如果是OPTIONS请求，直接放行（CORS预检请求）
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }
        
        // 获取请求路径
        String uri = request.getRequestURI();
        
        // 检查是否需要放行
        for (String path : EXCLUDE_PATHS) {
            if (uri.startsWith(path)) {
                return true;
            }
        }
        
        // 获取Token
        String token = request.getHeader(jwtProperties.getHeaderName());
        
        if (token == null || token.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"请先登录\"}");
            return false;
        }
        
        // 提取Token（去除Bearer前缀）
        String realToken = jwtUtils.extractToken(token);
        
        // 验证Token
        if (!jwtUtils.isTokenValid(realToken)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"Token已过期，请重新登录\"}");
            return false;
        }
        
        // 检查Token是否在Redis中（用于强制登出）
        String tokenKey = "token:" + jwtUtils.getUserIdFromToken(realToken);
        String cachedToken = (String) redisUtils.get(tokenKey);
        if (cachedToken == null || !cachedToken.equals(realToken)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"Token已失效，请重新登录\"}");
            return false;
        }
        
        // 将用户信息存入request，供后续使用
        Long userId = jwtUtils.getUserIdFromToken(realToken);
        String username = jwtUtils.getUsernameFromToken(realToken);
        request.setAttribute("userId", userId);
        request.setAttribute("username", username);
        
        return true;
    }
}