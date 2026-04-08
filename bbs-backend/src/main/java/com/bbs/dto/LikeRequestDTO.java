package com.bbs.dto;

import jakarta.validation.constraints.NotNull;

/**
 * 点赞请求DTO
 */
public class LikeRequestDTO {
    
    /** 目标类型: 1帖子 2回复 */
    @NotNull(message = "目标类型不能为空")
    private Integer targetType;
    
    /** 目标ID */
    @NotNull(message = "目标ID不能为空")
    private Long targetId;
    
    // Getters and Setters
    
    public Integer getTargetType() {
        return targetType;
    }
    
    public void setTargetType(Integer targetType) {
        this.targetType = targetType;
    }
    
    public Long getTargetId() {
        return targetId;
    }
    
    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }
}