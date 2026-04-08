package com.bbs.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

/**
 * 帖子实体类
 * 对应数据库表 bbs_topic
 */
@TableName("bbs_topic")
public class Topic {
    
    /** 帖子ID */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /** 所属版块ID */
    private Long forumId;
    
    /** 发布者ID */
    private Long userId;
    
    /** 帖子标题 */
    private String title;
    
    /** 正文内容 */
    private String content;
    
    /** 回复数 */
    private Integer replyCount;
    
    /** 浏览数 */
    private Integer viewCount;
    
    /** 点赞数 */
    private Integer likeCount;
    
    /** 置顶: 0普通 1全局置顶 2版块置顶 */
    private Integer isTop;
    
    /** 精华: 0普通 1精华 */
    private Integer isEssence;
    
    /** 锁定: 0开放 1锁定 */
    private Integer isLock;
    
    /** 最后回复人ID */
    private Long lastReplyUserId;
    
    /** 最后回复时间 */
    private LocalDateTime lastReplyTime;
    
    /** 状态: 0删除 1正常 */
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
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
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