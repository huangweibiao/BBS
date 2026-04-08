package com.bbs.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 版块创建请求DTO
 */
public class ForumCreateRequestDTO {
    
    /** 版块名称 */
    @NotBlank(message = "版块名称不能为空")
    @Size(max = 50, message = "版块名称长度不能超过50位")
    private String name;
    
    /** 版块描述 */
    @Size(max = 200, message = "版块描述长度不能超过200位")
    private String description;
    
    /** 版块图标URL */
    private String icon;
    
    /** 父版块ID */
    private Long parentId = 0L;
    
    /** 排序值 */
    private Integer sortOrder = 0;
    
    // Getters and Setters
    
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
}