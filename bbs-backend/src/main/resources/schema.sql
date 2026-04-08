-- BBS论坛系统表结构

-- ============================================
-- 1. 用户表 bbs_user
-- ============================================
CREATE TABLE IF NOT EXISTS bbs_user (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(32) NOT NULL COMMENT '用户名',
    password_hash VARCHAR(64) NOT NULL COMMENT '加密密码',
    nickname VARCHAR(32) NOT NULL COMMENT '昵称',
    email VARCHAR(64) NOT NULL COMMENT '邮箱',
    avatar VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    role TINYINT NOT NULL DEFAULT 0 COMMENT '角色: 0普通用户 1版主 2超级管理员',
    points INT NOT NULL DEFAULT 0 COMMENT '积分',
    post_count INT NOT NULL DEFAULT 0 COMMENT '发帖数',
    reply_count INT NOT NULL DEFAULT 0 COMMENT '回帖数',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0禁用 1正常',
    last_login_time DATETIME DEFAULT NULL COMMENT '最后登录时间',
    last_login_ip VARCHAR(45) DEFAULT NULL COMMENT '最后登录IP',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username),
    UNIQUE KEY uk_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ============================================
-- 2. 版块表 bbs_forum
-- ============================================
CREATE TABLE IF NOT EXISTS bbs_forum (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '版块ID',
    name VARCHAR(50) NOT NULL COMMENT '版块名称',
    description VARCHAR(200) DEFAULT NULL COMMENT '版块描述',
    icon VARCHAR(255) DEFAULT NULL COMMENT '版块图标URL',
    parent_id BIGINT NOT NULL DEFAULT 0 COMMENT '父版块ID,0为顶级',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序值',
    topic_count INT NOT NULL DEFAULT 0 COMMENT '主题帖数',
    post_count INT NOT NULL DEFAULT 0 COMMENT '总帖数',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0关闭 1开放',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='版块表';

-- ============================================
-- 3. 版主关联表 bbs_moderator
-- ============================================
CREATE TABLE IF NOT EXISTS bbs_moderator (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '版主用户ID',
    forum_id BIGINT NOT NULL COMMENT '负责版块ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '任命时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_forum (user_id, forum_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='版主关联表';

-- ============================================
-- 4. 帖子表 bbs_topic
-- ============================================
CREATE TABLE IF NOT EXISTS bbs_topic (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '帖子ID',
    forum_id BIGINT NOT NULL COMMENT '所属版块ID',
    user_id BIGINT NOT NULL COMMENT '发布者ID',
    title VARCHAR(120) NOT NULL COMMENT '帖子标题',
    content TEXT NOT NULL COMMENT '正文',
    reply_count INT NOT NULL DEFAULT 0 COMMENT '回复数',
    view_count INT NOT NULL DEFAULT 0 COMMENT '浏览数',
    like_count INT NOT NULL DEFAULT 0 COMMENT '点赞数',
    is_top TINYINT NOT NULL DEFAULT 0 COMMENT '置顶: 0普通 1全局置顶 2版块置顶',
    is_essence TINYINT NOT NULL DEFAULT 0 COMMENT '精华: 0普通 1精华',
    is_lock TINYINT NOT NULL DEFAULT 0 COMMENT '锁定: 0开放 1锁定',
    last_reply_user_id BIGINT DEFAULT NULL COMMENT '最后回复人ID',
    last_reply_time DATETIME DEFAULT NULL COMMENT '最后回复时间',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0删除 1正常',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_forum_created (forum_id, created_at),
    KEY idx_user_id (user_id),
    KEY idx_is_top (is_top),
    KEY idx_last_reply_time (last_reply_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='帖子表';

-- ============================================
-- 5. 回复表 bbs_reply
-- ============================================
CREATE TABLE IF NOT EXISTS bbs_reply (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '回复ID',
    topic_id BIGINT NOT NULL COMMENT '所属帖子ID',
    user_id BIGINT NOT NULL COMMENT '回复者ID',
    content TEXT NOT NULL COMMENT '回复内容',
    parent_reply_id BIGINT NOT NULL DEFAULT 0 COMMENT '父回复ID,0为一级评论',
    reply_to_user_id BIGINT DEFAULT NULL COMMENT '回复目标用户ID',
    like_count INT NOT NULL DEFAULT 0 COMMENT '点赞数',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0删除 1正常',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_topic_created (topic_id, created_at),
    KEY idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='回复表';

-- ============================================
-- 6. 点赞表 bbs_like
-- ============================================
CREATE TABLE IF NOT EXISTS bbs_like (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '点赞用户ID',
    target_type TINYINT NOT NULL COMMENT '目标类型: 1帖子 2回复',
    target_id BIGINT NOT NULL COMMENT '帖子/回复ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_target (user_id, target_type, target_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='点赞表';

-- ============================================
-- 7. 收藏表 bbs_favorite
-- ============================================
CREATE TABLE IF NOT EXISTS bbs_favorite (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '收藏用户ID',
    post_id BIGINT NOT NULL COMMENT '收藏帖子ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_post (user_id, post_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收藏表';

-- ============================================
-- 8. 消息表 bbs_message
-- ============================================
CREATE TABLE IF NOT EXISTS bbs_message (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    from_user_id BIGINT DEFAULT NULL COMMENT '发送者ID,NULL为系统通知',
    to_user_id BIGINT NOT NULL COMMENT '接收者ID',
    type TINYINT NOT NULL DEFAULT 1 COMMENT '类型: 1系统通知 2@提醒 3回复提醒 4私信',
    title VARCHAR(100) NOT NULL COMMENT '消息标题',
    content VARCHAR(500) NOT NULL COMMENT '消息内容',
    is_read TINYINT NOT NULL DEFAULT 0 COMMENT '状态: 0未读 1已读',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_to_user_read (to_user_id, is_read, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息表';

-- ============================================
-- 9. 积分记录表 bbs_points_log
-- ============================================
CREATE TABLE IF NOT EXISTS bbs_points_log (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '积分变动用户ID',
    points_change INT NOT NULL COMMENT '积分变动值',
    action_type TINYINT NOT NULL COMMENT '操作类型: 1发帖 2回帖 3加精 4置顶 5签到 6点赞(被赞)',
    description VARCHAR(255) DEFAULT NULL COMMENT '变动描述',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '变动时间',
    PRIMARY KEY (id),
    KEY idx_user_created (user_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='积分记录表';

-- ============================================
-- 10. 角色表 bbs_role
-- ============================================
CREATE TABLE IF NOT EXISTS bbs_role (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    role_name VARCHAR(32) NOT NULL COMMENT '角色名称',
    role_code VARCHAR(32) NOT NULL COMMENT '角色编码',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0禁用 1正常',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_code (role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- ============================================
-- 11. 权限表 bbs_permission
-- ============================================
CREATE TABLE IF NOT EXISTS bbs_permission (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '权限ID',
    perm_name VARCHAR(32) NOT NULL COMMENT '权限名称',
    perm_code VARCHAR(32) NOT NULL COMMENT '权限编码',
    perm_type TINYINT NOT NULL DEFAULT 1 COMMENT '类型: 1菜单 2按钮 3接口',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_perm_code (perm_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';

-- ============================================
-- 12. 用户-角色关联表 bbs_user_role
-- ============================================
CREATE TABLE IF NOT EXISTS bbs_user_role (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_role (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户-角色关联表';

-- ============================================
-- 13. 角色-权限关联表 bbs_role_permission
-- ============================================
CREATE TABLE IF NOT EXISTS bbs_role_permission (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    perm_id BIGINT NOT NULL COMMENT '权限ID',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_perm (role_id, perm_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色-权限关联表';

-- ============================================
-- 14. 签到记录表 bbs_signin_log
-- ============================================
CREATE TABLE IF NOT EXISTS bbs_signin_log (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '签到用户ID',
    sign_date DATE NOT NULL COMMENT '签到日期',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '签到时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_date (user_id, sign_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='签到记录表';