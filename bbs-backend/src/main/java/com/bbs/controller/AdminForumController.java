package com.bbs.controller;

import com.bbs.common.Result;
import com.bbs.dto.ForumCreateRequestDTO;
import com.bbs.dto.ForumVO;
import com.bbs.service.ForumService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 版块管理控制器
 * 处理版块的管理接口（需管理员权限）
 */
@RestController
@RequestMapping("/api/admin")
public class AdminForumController {
    
    @Resource
    private ForumService forumService;
    
    /**
     * 创建版块（需管理员权限）
     */
    @PostMapping("/forums")
    public Result<ForumVO> createForum(
            @Valid @RequestBody ForumCreateRequestDTO request,
            HttpServletRequest httpRequest) {
        // 简单检查角色，实际应从数据库或Redis获取
        Integer role = (Integer) httpRequest.getAttribute("role");
        if (role == null || role < 2) {
            return Result.forbidden("权限不足");
        }
        
        Long userId = (Long) httpRequest.getAttribute("userId");
        ForumVO forum = forumService.createForum(request, userId);
        return Result.success("版块创建成功", forum);
    }
    
    /**
     * 更新版块
     */
    @PutMapping("/forums/{forumId}")
    public Result<ForumVO> updateForum(
            @PathVariable Long forumId,
            @Valid @RequestBody ForumCreateRequestDTO request,
            HttpServletRequest httpRequest) {
        Integer role = (Integer) httpRequest.getAttribute("role");
        if (role == null || role < 2) {
            return Result.forbidden("权限不足");
        }
        
        ForumVO forum = forumService.updateForum(forumId, request);
        return Result.success("版块更新成功", forum);
    }
    
    /**
     * 删除版块
     */
    @DeleteMapping("/forums/{forumId}")
    public Result<Void> deleteForum(
            @PathVariable Long forumId,
            HttpServletRequest httpRequest) {
        Integer role = (Integer) httpRequest.getAttribute("role");
        if (role == null || role < 2) {
            return Result.forbidden("权限不足");
        }
        
        forumService.deleteForum(forumId);
        return Result.successMessage("版块删除成功");
    }
}