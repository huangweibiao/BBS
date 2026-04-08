package com.bbs.controller;

import com.bbs.common.Result;
import com.bbs.dto.LikeRequestDTO;
import com.bbs.service.LikeService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 点赞控制器
 */
@RestController
@RequestMapping("/api")
public class LikeController {
    
    @Resource
    private LikeService likeService;
    
    /**
     * 点赞或取消点赞
     */
    @PostMapping("/like")
    public Result<String> toggleLike(
            @Valid @RequestBody LikeRequestDTO request,
            HttpServletRequest httpRequest) {
        
        Long userId = (Long) httpRequest.getAttribute("userId");
        String result = likeService.toggleLike(request, userId);
        return Result.success(result);
    }
}