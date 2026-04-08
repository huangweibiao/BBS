package com.bbs.controller;

import com.bbs.common.Result;
import com.bbs.service.TopicService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

/**
 * 帖子管理控制器
 */
@RestController
@RequestMapping("/api/admin")
public class AdminTopicController {
    
    @Resource
    private TopicService topicService;
    
    /**
     * 设置帖子置顶
     */
    @PutMapping("/topics/{topicId}/top")
    public Result<Void> setTopicTop(
            @PathVariable Long topicId,
            @RequestParam Integer topType,
            HttpServletRequest request) {
        
        // 检查权限
        Integer role = (Integer) request.getAttribute("role");
        if (role == null || role < 1) {
            return Result.forbidden("权限不足，需要版主或管理员权限");
        }
        
        topicService.setTopicTop(topicId, topType);
        return Result.successMessage("置顶设置成功");
    }
    
    /**
     * 设置帖子精华
     */
    @PutMapping("/topics/{topicId}/essence")
    public Result<Void> setTopicEssence(
            @PathVariable Long topicId,
            @RequestParam Integer isEssence,
            HttpServletRequest request) {
        
        Integer role = (Integer) request.getAttribute("role");
        if (role == null || role < 1) {
            return Result.forbidden("权限不足，需要版主或管理员权限");
        }
        
        topicService.setTopicEssence(topicId, isEssence);
        return Result.successMessage("精华设置成功");
    }
    
    /**
     * 设置帖子锁定/解锁
     */
    @PutMapping("/topics/{topicId}/lock")
    public Result<Void> setTopicLock(
            @PathVariable Long topicId,
            @RequestParam Integer isLock,
            HttpServletRequest request) {
        
        Integer role = (Integer) request.getAttribute("role");
        if (role == null || role < 1) {
            return Result.forbidden("权限不足，需要版主或管理员权限");
        }
        
        topicService.setTopicLock(topicId, isLock);
        return Result.successMessage(isLock == 1 ? "帖子已锁定" : "帖子已解锁");
    }
}