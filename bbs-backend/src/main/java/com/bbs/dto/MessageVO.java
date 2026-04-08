package com.bbs.dto;

import java.time.LocalDateTime;

/**
 * 消息视图对象
 */
public class MessageVO {
    
    /** 消息ID */
    private Long id;
    
    /** 发送者ID */
    private Long fromUserId;
    
    /** 发送者用户名 */
    private String fromUsername;
    
    /** 发送者头像 */
    private String fromAvatar;
    
    /** 接收者ID */
    private Long toUserId;
    
    /** 类型 */
    private Integer type;
    
    /** 类型名称 */
    private String typeName;
    
    /** 消息标题 */
    private String title;
    
    /** 消息内容 */
    private String content;
    
    /** 状态 */
    private Integer isRead;
    
    /** 创建时间 */
    private LocalDateTime createdAt;
    
    // Getters and Setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getFromUserId() {
        return fromUserId;
    }
    
    public void setFromUserId(Long fromUserId) {
        this.fromUserId = fromUserId;
    }
    
    public String getFromUsername() {
        return fromUsername;
    }
    
    public void setFromUsername(String fromUsername) {
        this.fromUsername = fromUsername;
    }
    
    public String getFromAvatar() {
        return fromAvatar;
    }
    
    public void setFromAvatar(String fromAvatar) {
        this.fromAvatar = fromAvatar;
    }
    
    public Long getToUserId() {
        return toUserId;
    }
    
    public void setToUserId(Long toUserId) {
        this.toUserId = toUserId;
    }
    
    public Integer getType() {
        return type;
    }
    
    public void setType(Integer type) {
        this.type = type;
    }
    
    public String getTypeName() {
        return typeName;
    }
    
    public void setTypeName(String typeName) {
        this.typeName = typeName;
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
    
    public Integer getIsRead() {
        return isRead;
    }
    
    public void setIsRead(Integer isRead) {
        this.isRead = isRead;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}