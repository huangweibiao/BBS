package com.bbs.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 回复创建请求DTO
 */
public class ReplyCreateRequestDTO {
    
    /** 回复内容 */
    @NotBlank(message = "回复内容不能为空")
    private String content;
    
    /** 父回复ID（楼中楼回复时使用） */
    private Long parentReplyId;
    
    /** 回复目标用户ID（楼中楼回复时使用） */
    private Long replyToUserId;
    
    // Getters and Setters
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public Long getParentReplyId() {
        return parentReplyId;
    }
    
    public void setParentReplyId(Long parentReplyId) {
        this.parentReplyId = parentReplyId;
    }
    
    public Long getReplyToUserId() {
        return replyToUserId;
    }
    
    public void setReplyToUserId(Long replyToUserId) {
        this.replyToUserId = replyToUserId;
    }
}