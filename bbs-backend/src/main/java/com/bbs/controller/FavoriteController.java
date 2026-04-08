package com.bbs.controller;

import com.bbs.common.Result;
import com.bbs.dto.FavoriteRequestDTO;
import com.bbs.service.FavoriteService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 收藏控制器
 */
@RestController
@RequestMapping("/api")
public class FavoriteController {
    
    @Resource
    private FavoriteService favoriteService;
    
    /**
     * 收藏或取消收藏
     */
    @PostMapping("/favorite")
    public Result<String> toggleFavorite(
            @Valid @RequestBody FavoriteRequestDTO request,
            HttpServletRequest httpRequest) {
        
        Long userId = (Long) httpRequest.getAttribute("userId");
        String result = favoriteService.toggleFavorite(request, userId);
        return Result.success(result);
    }
}