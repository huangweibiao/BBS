package com.bbs.controller;

import com.bbs.common.Result;
import com.bbs.dto.ModeratorRequestDTO;
import com.bbs.entity.User;
import com.bbs.mapper.UserMapper;
import com.bbs.service.ModeratorService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 版主管理控制器
 */
@RestController
@RequestMapping("/api/admin")
public class AdminModeratorController {
    
    @Resource
    private ModeratorService moderatorService;
    
    @Resource
    private UserMapper userMapper;
    
    /**
     * 任命版主
     */
    @PostMapping("/moderators")
    public Result<Void> appointModerator(
            @Valid @RequestBody ModeratorRequestDTO request,
            HttpServletRequest httpRequest) {
        
        // 检查权限
        Integer role = (Integer) httpRequest.getAttribute("role");
        if (role == null || role < 2) {
            return Result.forbidden("权限不足，需要管理员权限");
        }
        
        moderatorService.appointModerator(request);
        return Result.successMessage("版主任命成功");
    }
    
    /**
     * 撤销版主
     */
    @DeleteMapping("/moderators")
    public Result<Void> removeModerator(
            @Valid @RequestBody ModeratorRequestDTO request,
            HttpServletRequest httpRequest) {
        
        Integer role = (Integer) httpRequest.getAttribute("role");
        if (role == null || role < 2) {
            return Result.forbidden("权限不足，需要管理员权限");
        }
        
        moderatorService.removeModerator(request);
        return Result.successMessage("版主撤销成功");
    }
}