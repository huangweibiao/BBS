package com.bbs.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 帖子创建请求DTO
 */
public class TopicCreateRequestDTO {
    
    /** 版块ID */
    @NotNull(message = "版块ID不能为空")
    private Long forumId;
    
    /** 帖子标题 */
    @NotBlank(message = "标题不能为空")
    @Size(max = 120, message = "标题长度不能超过120位")
    private String title;
    
    /** 帖子内容 */
    @NotBlank(message = "内容不能为空")
    private String content;
    
    // Getters and Setters
    
    public Long getForumId() {
        return forumId;
    }
    
    public void setForumId(Long forumId) {
        this.forumId = forumId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
}