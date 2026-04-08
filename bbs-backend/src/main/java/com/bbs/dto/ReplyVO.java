package com.bbs.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 回复视图对象
 */
public class ReplyVO {
    
    /** 回复ID */
    private Long id;
    
    /** 所属帖子ID */
    private Long topicId;
    
    /** 回复者ID */
    private Long userId;
    
    /** 回复者用户名 */
    private String username;
    
    /** 回复者昵称 */
    private String nickname;
    
    /** 回复者头像 */
    private String avatar;
    
    /** 回复内容 */
    private String content;
    
    /** 父回复ID */
    private Long parentReplyId;
    
    /** 父回复者用户名 */
    private String parentUsername;
    
    /** 回复目标用户ID */
    private Long replyToUserId;
    
    /** 回复目标用户名 */
    private String replyToUsername;
    
    /** 点赞数 */
    private Integer likeCount;
    
    /** 状态 */
    private Integer status;
    
    /** 创建时间 */
    private LocalDateTime createdAt;
    
    /** 子回复列表（楼中楼） */
    private List<ReplyVO> children;
    
    // Getters and Setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getTopicId() {
        return topicId;
    }
    
    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getNickname() {
        return nickname;
    }
    
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    
    public String getAvatar() {
        return avatar;
    }
    
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    
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
    
    public String getParentUsername() {
        return parentUsername;
    }
    
    public void setParentUsername(String parentUsername) {
        this.parentUsername = parentUsername;
    }
    
    public Long getReplyToUserId() {
        return replyToUserId;
    }
    
    public void setReplyToUserId(Long replyToUserId) {
        this.replyToUserId = replyToUserId;
    }
    
    public String getReplyToUsername() {
        return replyToUsername;
    }
    
    public void setReplyToUsername(String replyToUsername) {
        this.replyToUsername = replyToUsername;
    }
    
    public Integer getLikeCount() {
        return likeCount;
    }
    
    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public List<ReplyVO> getChildren() {
        return children;
    }
    
    public void setChildren(List<ReplyVO> children) {
        this.children = children;
    }
}