package com.bbs.controller;

import com.bbs.common.Result;
import com.bbs.dto.MessageVO;
import com.bbs.service.MessageService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 消息控制器
 */
@RestController
@RequestMapping("/api")
public class MessageController {
    
    @Resource
    private MessageService messageService;
    
    /**
     * 获取消息列表
     */
    @GetMapping("/messages")
    public Result<Map<String, Object>> getMessages(
            HttpServletRequest httpRequest,
            @RequestParam(required = false) Integer type,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        
        Long userId = (Long) httpRequest.getAttribute("userId");
        List<MessageVO> list = messageService.getMessages(userId, type, pageNum, pageSize);
        Long unreadCount = messageService.getUnreadCount(userId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("unreadCount", unreadCount);
        
        return Result.success(result);
    }
    
    /**
     * 获取未读消息数量
     */
    @GetMapping("/messages/unread")
    public Result<Long> getUnreadCount(HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        Long count = messageService.getUnreadCount(userId);
        return Result.success(count);
    }
    
    /**
     * 标记消息为已读
     */
    @PutMapping("/messages/{messageId}/read")
    public Result<Void> markAsRead(
            @PathVariable Long messageId,
            HttpServletRequest httpRequest) {
        
        Long userId = (Long) httpRequest.getAttribute("userId");
        messageService.markAsRead(messageId, userId);
        return Result.successMessage("标记已读成功");
    }
    
    /**
     * 标记所有消息为已读
     */
    @PutMapping("/messages/read-all")
    public Result<Void> markAllAsRead(HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        messageService.markAllAsRead(userId);
        return Result.successMessage("全部标记已读成功");
    }
}