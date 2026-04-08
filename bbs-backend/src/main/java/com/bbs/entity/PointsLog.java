package com.bbs.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

/**
 * 积分记录实体类
 * 对应数据库表 bbs_points_log
 */
@TableName("bbs_points_log")
public class PointsLog {
    
    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /** 积分变动用户ID */
    private Long userId;
    
    /** 积分变动值 */
    private Integer pointsChange;
    
    /** 操作类型: 1发帖 2回帖 3加精 4置顶 5签到 6点赞(被赞) */
    private Integer actionType;
    
    /** 变动描述 */
    private String description;
    
    /** 变动时间 */
    private LocalDateTime createdAt;
    
    // Getters and Setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public Integer getPointsChange() {
        return pointsChange;
    }
    
    public void setPointsChange(Integer pointsChange) {
        this.pointsChange = pointsChange;
    }
    
    public Integer getActionType() {
        return actionType;
    }
    
    public void setActionType(Integer actionType) {
        this.actionType = actionType;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}