package com.bbs.controller;

import com.bbs.common.Result;
import com.bbs.dto.TopicCreateRequestDTO;
import com.bbs.dto.TopicUpdateRequestDTO;
import com.bbs.dto.TopicVO;
import com.bbs.service.TopicService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 帖子控制器
 */
@RestController
@RequestMapping("/api")
public class TopicController {
    
    @Resource
    private TopicService topicService;
    
    /**
     * 创建帖子
     */
    @PostMapping("/topics")
    public Result<TopicVO> createTopic(
            @Valid @RequestBody TopicCreateRequestDTO request,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        TopicVO topic = topicService.createTopic(request, userId);
        return Result.success("帖子发布成功", topic);
    }
    
    /**
     * 获取帖子详情
     */
    @GetMapping("/topics/{topicId}")
    public Result<TopicVO> getTopic(@PathVariable Long topicId) {
        TopicVO topic = topicService.getTopicById(topicId);
        // 增加浏览数
        topicService.incrementViewCount(topicId);
        return Result.success(topic);
    }
    
    /**
     * 更新帖子
     */
    @PutMapping("/topics/{topicId}")
    public Result<TopicVO> updateTopic(
            @PathVariable Long topicId,
            @Valid @RequestBody TopicUpdateRequestDTO request,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        TopicVO topic = topicService.updateTopic(topicId, request, userId);
        return Result.success("帖子更新成功", topic);
    }
    
    /**
     * 删除帖子
     */
    @DeleteMapping("/topics/{topicId}")
    public Result<Void> deleteTopic(
            @PathVariable Long topicId,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        topicService.deleteTopic(topicId, userId);
        return Result.successMessage("帖子删除成功");
    }
    
    /**
     * 获取版块帖子列表
     */
    @GetMapping("/forums/{forumId}/topics")
    public Result<Map<String, Object>> getForumTopics(
            @PathVariable Long forumId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(defaultValue = "latest") String sort) {
        
        List<TopicVO> list = topicService.getTopicsByForumId(forumId, pageNum, pageSize, sort);
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        
        return Result.success(result);
    }
    
    /**
     * 获取热门帖子列表
     */
    @GetMapping("/topics/hot")
    public Result<List<TopicVO>> getHotTopics(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        
        List<TopicVO> list = topicService.getHotTopics(pageNum, pageSize);
        return Result.success(list);
    }
}