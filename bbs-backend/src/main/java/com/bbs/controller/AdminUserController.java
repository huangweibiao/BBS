package com.bbs.controller;

import com.bbs.common.Result;
import com.bbs.entity.User;
import com.bbs.mapper.UserMapper;
import com.bbs.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制器
 */
@RestController
@RequestMapping("/api/admin")
public class AdminUserController {
    
    @Resource
    private UserMapper userMapper;
    
    @Resource
    private UserService userService;
    
    /**
     * 禁用/启用用户
     */
    @PutMapping("/users/{userId}/status")
    public Result<Void> updateUserStatus(
            @PathVariable Long userId,
            @RequestParam Integer status,
            HttpServletRequest httpRequest) {
        
        // 检查权限
        Integer role = (Integer) httpRequest.getAttribute("role");
        if (role == null || role < 2) {
            return Result.forbidden("权限不足，需要管理员权限");
        }
        
        // 不能禁用管理员
        User targetUser = userMapper.selectById(userId);
        if (targetUser == null) {
            return Result.notFound("用户不存在");
        }
        if (targetUser.getRole() == 2) {
            return Result.forbidden("不能禁用管理员账号");
        }
        
        // 更新状态
        targetUser.setStatus(status);
        userMapper.updateById(targetUser);
        
        // 清除用户缓存
        com.bbs.common.RedisUtils redisUtils = new com.bbs.common.RedisUtils();
        // 注意：这里需要注入，实际应该通过service调用
        
        return Result.successMessage(status == 0 ? "用户已禁用" : "用户已启用");
    }
}