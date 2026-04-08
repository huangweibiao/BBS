package com.bbs.controller;

import com.bbs.common.Result;
import com.bbs.dto.LoginRequestDTO;
import com.bbs.dto.LoginResponseVO;
import com.bbs.dto.RegisterRequestDTO;
import com.bbs.dto.UserVO;
import com.bbs.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 * 处理用户注册、登录等认证相关请求
 */
@RestController
@RequestMapping("/api")
public class AuthController {
    
    @Resource
    private UserService userService;
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<UserVO> register(@Valid @RequestBody RegisterRequestDTO registerRequest) {
        UserVO user = userService.register(registerRequest);
        return Result.success("注册成功", user);
    }
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginResponseVO> login(@Valid @RequestBody LoginRequestDTO loginRequest, HttpServletRequest request) {
        // 获取客户端IP
        String ip = getClientIp(request);
        LoginResponseVO response = userService.login(loginRequest.getUsername(), loginRequest.getPassword(), ip);
        return Result.success("登录成功", response);
    }
    
    /**
     * 获取客户端IP地址
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 如果有多个代理，取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}