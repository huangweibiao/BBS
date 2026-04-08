package com.bbs.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

/**
 * 权限实体类
 * 对应数据库表 bbs_permission
 */
@TableName("bbs_permission")
public class Permission {
    
    /** 权限ID */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /** 权限名称 */
    private String permName;
    
    /** 权限编码 */
    private String permCode;
    
    /** 类型: 1菜单 2按钮 3接口 */
    private Integer permType;
    
    /** 创建时间 */
    private LocalDateTime createdAt;
    
    // Getters and Setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getPermName() {
        return permName;
    }
    
    public void setPermName(String permName) {
        this.permName = permName;
    }
    
    public String getPermCode() {
        return permCode;
    }
    
    public void setPermCode(String permCode) {
        this.permCode = permCode;
    }
    
    public Integer getPermType() {
        return permType;
    }
    
    public void setPermType(Integer permType) {
        this.permType = permType;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}