package com.bbs.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 版块视图对象
 */
public class ForumVO {
    
    /** 版块ID */
    private Long id;
    
    /** 版块名称 */
    private String name;
    
    /** 版块描述 */
    private String description;
    
    /** 版块图标URL */
    private String icon;
    
    /** 父版块ID */
    private Long parentId;
    
    /** 排序值 */
    private Integer sortOrder;
    
    /** 主题帖数 */
    private Integer topicCount;
    
    /** 总帖数 */
    private Integer postCount;
    
    /** 状态 */
    private Integer status;
    
    /** 创建时间 */
    private LocalDateTime createdAt;
    
    /** 子版块列表 */
    private List<ForumVO> children;
    
    // Getters and Setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getIcon() {
        return icon;
    }
    
    public void setIcon(String icon) {
        this.icon = icon;
    }
    
    public Long getParentId() {
        return parentId;
    }
    
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
    
    public Integer getSortOrder() {
        return sortOrder;
    }
    
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
    
    public Integer getTopicCount() {
        return topicCount;
    }
    
    public void setTopicCount(Integer topicCount) {
        this.topicCount = topicCount;
    }
    
    public Integer getPostCount() {
        return postCount;
    }
    
    public void setPostCount(Integer postCount) {
        this.postCount = postCount;
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
    
    public List<ForumVO> getChildren() {
        return children;
    }
    
    public void setChildren(List<ForumVO> children) {
        this.children = children;
    }
    
    /**
     * 将Forum实体转换为ForumVO
     */
    public static ForumVO fromEntity(com.bbs.entity.Forum forum) {
        ForumVO vo = new ForumVO();
        vo.setId(forum.getId());
        vo.setName(forum.getName());
        vo.setDescription(forum.getDescription());
        vo.setIcon(forum.getIcon());
        vo.setParentId(forum.getParentId());
        vo.setSortOrder(forum.getSortOrder());
        vo.setTopicCount(forum.getTopicCount());
        vo.setPostCount(forum.getPostCount());
        vo.setStatus(forum.getStatus());
        vo.setCreatedAt(forum.getCreatedAt());
        return vo;
    }
}