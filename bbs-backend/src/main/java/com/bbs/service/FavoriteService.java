package com.bbs.service;

import com.bbs.dto.FavoriteRequestDTO;

/**
 * 收藏服务接口
 */
public interface FavoriteService {
    
    /**
     * 收藏或取消收藏
     * @param request 收藏请求
     * @param userId 用户ID
     * @return 操作结果
     */
    String toggleFavorite(FavoriteRequestDTO request, Long userId);
    
    /**
     * 检查用户是否已收藏
     */
    boolean hasFavorited(Long userId, Long postId);
}