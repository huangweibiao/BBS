-- BBS论坛初始化数据

-- 初始化管理员用户 (密码: admin123)
INSERT IGNORE INTO bbs_user (username, password_hash, nickname, email, role, status) VALUES
('admin', '0192023a7bbd73250516f069df18b500', '管理员', 'admin@bbs.com', 2, 1);

-- 初始化版块数据
INSERT IGNORE INTO bbs_forum (name, description, parent_id, sort_order, status) VALUES
('默认版块', '默认讨论版块', 0, 1, 1),
('技术交流', '技术相关讨论', 0, 2, 1),
('生活娱乐', '生活娱乐话题', 0, 3, 1);

-- 初始化角色
INSERT IGNORE INTO bbs_role (role_name, role_code, status) VALUES
('普通用户', 'user', 1),
('版主', 'moderator', 1),
('超级管理员', 'admin', 1);

-- 初始化权限
INSERT IGNORE INTO bbs_permission (perm_name, perm_code, perm_type) VALUES
('帖子管理', 'topic:manage', 2),
('版块管理', 'forum:manage', 2),
('用户管理', 'user:manage', 2),
('帖子置顶', 'topic:top', 2),
('帖子加精', 'topic:essence', 2),
('帖子锁定', 'topic:lock', 2),
('数据统计', 'data:statistics', 2);

-- 初始化管理员角色权限
INSERT IGNORE INTO bbs_role_permission (role_id, perm_id) VALUES
(3, 1), (3, 2), (3, 3), (3, 4), (3, 5), (3, 6), (3, 7);

-- 初始化版主角色权限
INSERT IGNORE INTO bbs_role_permission (role_id, perm_id) VALUES
(2, 1), (2, 4), (2, 5), (2, 6);