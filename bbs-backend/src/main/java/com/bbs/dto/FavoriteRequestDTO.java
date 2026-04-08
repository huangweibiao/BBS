package com.bbs.dto;

import jakarta.validation.constraints.NotNull;

/**
 * 收藏请求DTO
 */
public class FavoriteRequestDTO {
    
    /** 帖子ID */
    @NotNull(message = "帖子ID不能为空")
    private Long postId;
    
    // Getters and Setters
    
    public Long getPostId() {
        return postId;
    }
    
    public void setPostId(Long postId) {
        this.postId = postId;
    }
}