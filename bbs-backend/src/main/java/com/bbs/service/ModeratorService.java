package com.bbs.service;

import com.bbs.dto.ModeratorRequestDTO;

/**
 * 版主服务接口
 */
public interface ModeratorService {
    
    /**
     * 任命版主
     */
    void appointModerator(ModeratorRequestDTO request);
    
    /**
     * 撤销版主
     */
    void removeModerator(ModeratorRequestDTO request);
    
    /**
     * 检查用户是否为版主
     */
    boolean isModerator(Long userId, Long forumId);
}