package com.bbs.service;

import com.bbs.dto.LikeRequestDTO;

/**
 * 点赞服务接口
 */
public interface LikeService {
    
    /**
     * 点赞或取消点赞
     * @param request 点赞请求
     * @param userId 用户ID
     * @return 操作结果
     */
    String toggleLike(LikeRequestDTO request, Long userId);
    
    /**
     * 检查用户是否已点赞
     */
    boolean hasLiked(Long userId, Integer targetType, Long targetId);
}