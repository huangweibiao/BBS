package com.bbs.controller;

import com.bbs.common.Result;
import com.bbs.dto.UserVO;
import com.bbs.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 * 处理用户资料、签到等请求
 */
@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @Resource
    private UserService userService;
    
    /**
     * 获取当前用户资料
     */
    @GetMapping("/profile")
    public Result<UserVO> getProfile(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        UserVO userVO = userService.getUserById(userId);
        return Result.success(userVO);
    }
    
    /**
     * 修改当前用户资料
     */
    @PutMapping("/profile")
    public Result<UserVO> updateProfile(
            HttpServletRequest request,
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) String avatar,
            @RequestParam(required = false) String email) {
        Long userId = (Long) request.getAttribute("userId");
        UserVO userVO = userService.updateProfile(userId, nickname, avatar, email);
        return Result.success("资料更新成功", userVO);
    }
    
    /**
     * 每日签到
     */
    @PostMapping("/sign")
    public Result<String> signIn(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String result = userService.signIn(userId);
        return Result.success(result);
    }
}