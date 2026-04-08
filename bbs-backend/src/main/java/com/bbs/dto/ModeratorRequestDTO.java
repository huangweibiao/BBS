package com.bbs.dto;

import jakarta.validation.constraints.NotNull;

/**
 * 版主任命请求DTO
 */
public class ModeratorRequestDTO {
    
    /** 用户ID */
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    
    /** 版块ID */
    @NotNull(message = "版块ID不能为空")
    private Long forumId;
    
    // Getters and Setters
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public Long getForumId() {
        return forumId;
    }
    
    public void setForumId(Long forumId) {
        this.forumId = forumId;
    }
}