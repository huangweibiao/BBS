package com.bbs.controller;

import com.bbs.common.Result;
import com.bbs.dto.ReplyCreateRequestDTO;
import com.bbs.dto.ReplyVO;
import com.bbs.service.ReplyService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 回复控制器
 */
@RestController
@RequestMapping("/api")
public class ReplyController {
    
    @Resource
    private ReplyService replyService;
    
    /**
     * 创建回复
     */
    @PostMapping("/topics/{topicId}/replies")
    public Result<ReplyVO> createReply(
            @PathVariable Long topicId,
            @Valid @RequestBody ReplyCreateRequestDTO request,
            HttpServletRequest httpRequest) {
        
        Long userId = (Long) httpRequest.getAttribute("userId");
        ReplyVO reply = replyService.createReply(topicId, request, userId);
        return Result.success("回复成功", reply);
    }
    
    /**
     * 获取帖子回复列表
     */
    @GetMapping("/topics/{topicId}/replies")
    public Result<Map<String, Object>> getReplies(@PathVariable Long topicId) {
        List<ReplyVO> list = replyService.getRepliesByTopicId(topicId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", list.size());
        
        return Result.success(result);
    }
    
    /**
     * 删除回复
     */
    @DeleteMapping("/replies/{replyId}")
    public Result<Void> deleteReply(
            @PathVariable Long replyId,
            HttpServletRequest httpRequest) {
        
        Long userId = (Long) httpRequest.getAttribute("userId");
        replyService.deleteReply(replyId, userId);
        return Result.successMessage("删除成功");
    }
}