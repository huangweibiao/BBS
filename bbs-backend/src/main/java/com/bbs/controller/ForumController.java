package com.bbs.controller;

import com.bbs.common.Result;
import com.bbs.dto.ForumVO;
import com.bbs.service.ForumService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 版块控制器
 * 处理版块相关公开接口
 */
@RestController
@RequestMapping("/api")
public class ForumController {
    
    @Resource
    private ForumService forumService;
    
    /**
     * 获取版块树形列表（公开接口）
     */
    @GetMapping("/forums")
    public Result<List<ForumVO>> getForums() {
        List<ForumVO> list = forumService.getForumTree();
        return Result.success(list);
    }
    
    /**
     * 获取版块详情
     */
    @GetMapping("/forums/{forumId}")
    public Result<ForumVO> getForumDetail(@PathVariable Long forumId) {
        ForumVO forum = forumService.getForumById(forumId);
        return Result.success(forum);
    }
}