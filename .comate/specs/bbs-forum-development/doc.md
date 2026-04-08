# BBS论坛系统开发规范

## 1. 项目概述

- **项目名称**: BBS论坛系统
- **项目类型**: 前后端分离的全栈应用
- **技术栈**:
  - 后端: Spring Boot 3.5.11 + MyBatis-Plus + MySQL 8 + Redis
  - 前端: Vue 3 + Element Plus + Axios
  - 构建工具: Maven + Vite

## 2. 模块划分

### 2.1 后端模块结构

```
com.bbs
├── controller      # 控制层（REST接口）
├── service         # 业务层（接口+实现）
├── domain          # 领域层（实体/枚举/业务规则）
├── repository      # 仓储层（数据访问接口）
├── mapper          # 数据访问层（MyBatis映射）
├── entity          # 数据库实体类
├── dto             # 数据传输对象（入参/出参）
├── config          # 配置类（Redis/ES/MQ）
├── interceptor     # 拦截器（登录/权限/限流）
├── common          # 通用工具类/常量/异常
├── task            # 定时任务（积分结算/热帖计算）
└── infrastructure  # 基础设施（缓存/消息队列实现）
```

### 2.2 数据库模块（10张表）

1. `bbs_user` - 用户表
2. `bbs_forum` - 版块表
3. `bbs_moderator` - 版主关联表
4. `bbs_topic` - 帖子表
5. `bbs_reply` - 回复表
6. `bbs_like` - 点赞表
7. `bbs_favorite` - 收藏表
8. `bbs_message` - 消息表
9. `bbs_points_log` - 积分记录表
10. RBAC权限表：`bbs_role`, `bbs_permission`, `bbs_user_role`, `bbs_role_permission`

## 3. 功能模块优先级

### 第一阶段：基础功能
1. 用户系统（注册/登录/JWT认证）
2. 版块管理（树形结构展示）
3. 帖子系统（CRUD + 置顶/加精/锁定）

### 第二阶段：交互功能
4. 评论系统（楼中楼两级结构）
5. 点赞/收藏功能
6. 消息通知系统

### 第三阶段：高级功能
7. 搜索功能（Elasticsearch）
8. 热帖排行（Redis缓存）
9. 积分系统
10. 管理后台

## 4. 核心接口设计

### 公开接口
- POST /api/register - 用户注册
- POST /api/login - 用户登录
- GET /api/forums - 版块列表
- GET /api/forums/{forumId}/topics - 版块帖子列表
- GET /api/topics/{topicId} - 帖子详情
- GET /api/topics/hot - 热门帖子列表

### 登录后接口
- POST /api/topics - 发布帖子
- PUT /api/topics/{topicId} - 编辑帖子
- DELETE /api/topics/{topicId} - 删除帖子
- POST /api/topics/{topicId}/replies - 回复帖子
- POST /api/like - 点赞/取消点赞
- POST /api/favorite - 收藏/取消收藏
- GET /api/messages - 消息列表
- GET /api/user/profile - 个人资料
- PUT /api/user/profile - 修改资料
- POST /api/user/sign - 每日签到

### 管理接口
- PUT /api/admin/topics/{topicId}/top - 设置置顶
- PUT /api/admin/topics/{topicId}/essence - 设置精华
- PUT /api/admin/topics/{topicId}/lock - 锁定/解锁
- POST /api/admin/forums - 创建版块
- PUT /api/admin/forums/{forumId} - 编辑版块
- POST /api/admin/moderators - 任命版主
- DELETE /api/admin/moderators - 撤销版主
- PUT /api/admin/users/{userId}/status - 禁用/启用用户

## 5. 开发顺序

1. 项目基础架构搭建（Spring Boot项目初始化）
2. 数据库设计和实体类生成
3. 通用组件开发（统一响应、异常处理、Redis配置）
4. 用户模块开发（注册、登录、权限拦截）
5. 版块模块开发
6. 帖子模块开发
7. 评论模块开发
8. 互动功能开发（点赞、收藏、消息）
9. 积分和签到功能
10. 热帖排行功能
11. 管理后台开发
12. 前端开发

## 6. 配置文件说明

- application.yml - 主配置文件
- application-dev.yml - 开发环境配置
- application-prod.yml - 生产环境配置

## 7. 核心业务规则

1. 积分规则：发帖+5，回帖+2，帖子加精+10，每日签到+1，被点赞（每10次）+1
2. 置顶优先级：全局置顶 > 版块置顶 > 普通帖
3. 回复规则：帖子锁定后禁止新增回复
4. 消息规则：回复帖子时通知楼主，@用户时通知目标用户

## 8. 安全设计

- JWT Token认证
- 密码MD5加密存储
- 接口限流（Redis计数器）
- XSS防护（Jsoup过滤）
- SQL注入防护（MyBatis #{}参数绑定）

## 9. 其他要求
- 代码需要加注释
- 不使用Lombok
- 不使用Hutool