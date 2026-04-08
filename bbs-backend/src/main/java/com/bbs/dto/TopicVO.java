package com.bbs.dto;

import java.time.LocalDateTime;

/**
 * 帖子视图对象
 */
public class TopicVO {
    
    /** 帖子ID */
    private Long id;
    
    /** 版块ID */
    private Long forumId;
    
    /** 版块名称 */
    private String forumName;
    
    /** 发布者ID */
    private Long userId;
    
    /** 发布者用户名 */
    private String username;
    
    /** 发布者昵称 */
    private String nickname;
    
    /** 发布者头像 */
    private String avatar;
    
    /** 帖子标题 */
    private String title;
    
    /** 帖子内容 */
    private String content;
    
    /** 回复数 */
    private Integer replyCount;
    
    /** 浏览数 */
    private Integer viewCount;
    
    /** 点赞数 */
    private Integer likeCount;
    
    /** 置顶状态 */
    private Integer isTop;
    
    /** 精华状态 */
    private Integer isEssence;
    
    /** 锁定状态 */
    private Integer isLock;
    
    /** 最后回复人ID */
    private Long lastReplyUserId;
    
    /** 最后回复人用户名 */
    private String lastReplyUsername;
    
    /** 最后回复时间 */
    private LocalDateTime lastReplyTime;
    
    /** 状态 */
    private Integer status;
    
    /** 创建时间 */
    private LocalDateTime createdAt;
    
    /** 更新时间 */
    private LocalDateTime updatedAt;
    
    // Getters and Setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getForumId() {
        return forumId;
    }
    
    public void setForumId(Long forumId) {
        this.forumId = forumId;
    }
    
    public String getForumName() {
        return forumName;
    }
    
    public void setForumName(String forumName) {
        this.forumName = forumName;
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
    
    public Integer getReplyCount() {
        return replyCount;
    }
    
    public void setReplyCount(Integer replyCount) {
        this.replyCount = replyCount;
    }
    
    public Integer getViewCount() {
        return viewCount;
    }
    
    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }
    
    public Integer getLikeCount() {
        return likeCount;
    }
    
    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }
    
    public Integer getIsTop() {
        return isTop;
    }
    
    public void setIsTop(Integer isTop) {
        this.isTop = isTop;
    }
    
    public Integer getIsEssence() {
        return isEssence;
    }
    
    public void setIsEssence(Integer isEssence) {
        this.isEssence = isEssence;
    }
    
    public Integer getIsLock() {
        return isLock;
    }
    
    public void setIsLock(Integer isLock) {
        this.isLock = isLock;
    }
    
    public Long getLastReplyUserId() {
        return lastReplyUserId;
    }
    
    public void setLastReplyUserId(Long lastReplyUserId) {
        this.lastReplyUserId = lastReplyUserId;
    }
    
    public String getLastReplyUsername() {
        return lastReplyUsername;
    }
    
    public void setLastReplyUsername(String lastReplyUsername) {
        this.lastReplyUsername = lastReplyUsername;
    }
    
    public LocalDateTime getLastReplyTime() {
        return lastReplyTime;
    }
    
    public void setLastReplyTime(LocalDateTime lastReplyTime) {
        this.lastReplyTime = lastReplyTime;
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
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}