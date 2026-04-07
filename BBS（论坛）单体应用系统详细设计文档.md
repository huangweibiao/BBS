# BBS（论坛）单体应用系统详细设计文档

## 一、系统概述

本设计为面向生产级的BBS（电子公告板系统）单体应用，采用**模块化单体（Modular Monolith）** 架构，兼顾易用性与可扩展性。核心目标是实现功能完整、性能稳定、可平滑演进为微服务的论坛系统，技术栈推荐 Spring Boot + MyBatis-Plus + MySQL + Redis（核心）+ Elasticsearch（搜索），前端可选 React/Vue（前后端分离）或 Thymeleaf（后端渲染）。

## 二、核心功能

- 用户系统：注册/登录/权限/个人资料/积分

- 版块管理：分类CRUD/版主管理/树形结构

- 帖子系统：发布/编辑/删除/置顶/加精/锁定

- 评论（回复）系统：楼中楼（两级结构）/点赞

- 互动功能：点赞/收藏/私信/通知

- 搜索功能：标题/内容全文检索

- 管理后台：审核/封禁/权限配置

## 三、整体架构

### 3.1 架构分层（请求链路）

```Plain Text

[ Client (Web / App) ]
          ↓
[ API Gateway / Controller Layer ]  // 接口层，处理请求与响应
          ↓
[ Application Service Layer ]       // 业务层，核心逻辑
          ↓
[ Domain Layer ]                    // 领域层，实体与业务规则
          ↓
[ Infrastructure Layer ]            // 基础设施层，对接缓存/数据库/MQ
          ↓
[ DB / Cache / MQ / Search ]        // 存储层
```

### 3.2 代码目录结构（DDD-lite）

```Plain Text

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

### 3.3 部署架构（单体）

```Plain Text

Nginx（反向代理/静态资源）
  ↓
App（Spring Boot 应用）
  ↓
MySQL（主库） + Redis（缓存） + Elasticsearch（搜索） + Kafka（MQ，可选）
```

- 容器化：Docker（基础），Kubernetes（后期扩展）

## 四、数据库设计（完整表结构）

### 4.1 用户表 (bbs_user)

|字段名|类型|长度|允许空|默认值|说明|
|---|---|---|---|---|---|
|id|BIGINT|20|NOT NULL|AUTO_INCREMENT|用户ID（主键）|
|username|VARCHAR|32|NOT NULL|-|用户名（唯一索引）|
|password_hash|VARCHAR|64|NOT NULL|-|加密密码（MD5/SHA256）|
|nickname|VARCHAR|32|NOT NULL|-|昵称|
|email|VARCHAR|64|NOT NULL|-|邮箱（唯一索引）|
|avatar|VARCHAR|255|YES|NULL|头像URL|
|role|TINYINT|1|NOT NULL|0|0普通用户 1版主 2超级管理员|
|points|INT|11|NOT NULL|0|积分|
|post_count|INT|11|NOT NULL|0|发帖数|
|reply_count|INT|11|NOT NULL|0|回帖数|
|status|TINYINT|1|NOT NULL|1|0禁用 1正常|
|last_login_time|DATETIME|-|YES|NULL|最后登录时间|
|last_login_ip|VARCHAR|45|YES|NULL|最后登录IP|
|created_at|DATETIME|-|NOT NULL|CURRENT_TIMESTAMP|注册时间|
|updated_at|DATETIME|-|NOT NULL|CURRENT_TIMESTAMP ON UPDATE|更新时间|
### 4.2 版块表 (bbs_forum)

|字段名|类型|长度|允许空|默认值|说明|
|---|---|---|---|---|---|
|id|BIGINT|20|NOT NULL|AUTO_INCREMENT|版块ID（主键）|
|name|VARCHAR|50|NOT NULL|-|版块名称|
|description|VARCHAR|200|YES|NULL|版块描述|
|icon|VARCHAR|255|YES|NULL|版块图标URL|
|parent_id|BIGINT|20|YES|0|父版块ID（0为顶级，树形结构）|
|sort_order|INT|11|NOT NULL|0|排序值（越小越靠前）|
|topic_count|INT|11|NOT NULL|0|主题帖数|
|post_count|INT|11|NOT NULL|0|总帖数（含回复）|
|status|TINYINT|1|NOT NULL|1|0关闭 1开放|
|created_at|DATETIME|-|NOT NULL|CURRENT_TIMESTAMP|创建时间|
|updated_at|DATETIME|-|NOT NULL|CURRENT_TIMESTAMP ON UPDATE|更新时间|
|索引|-|-|-|-|parent_id（普通索引）|
### 4.3 版主关联表 (bbs_moderator)

|字段名|类型|长度|允许空|默认值|说明|
|---|---|---|---|---|---|
|id|BIGINT|20|NOT NULL|AUTO_INCREMENT|主键ID|
|user_id|BIGINT|20|NOT NULL|-|版主用户ID|
|forum_id|BIGINT|20|NOT NULL|-|负责版块ID|
|created_at|DATETIME|-|NOT NULL|CURRENT_TIMESTAMP|任命时间|
|索引|-|-|-|-|(user_id, forum_id)（联合唯一索引）|
### 4.4 帖子表 (bbs_topic)

|字段名|类型|长度|允许空|默认值|说明|
|---|---|---|---|---|---|
|id|BIGINT|20|NOT NULL|AUTO_INCREMENT|帖子ID（主键）|
|forum_id|BIGINT|20|NOT NULL|-|所属版块ID|
|user_id|BIGINT|20|NOT NULL|-|发布者ID|
|title|VARCHAR|120|NOT NULL|-|帖子标题|
|content|TEXT|-|NOT NULL|-|正文（Markdown/HTML）|
|reply_count|INT|11|NOT NULL|0|回复数|
|view_count|INT|11|NOT NULL|0|浏览数|
|like_count|INT|11|NOT NULL|0|点赞数|
|is_top|TINYINT|1|NOT NULL|0|0普通 1全局置顶 2版块置顶|
|is_essence|TINYINT|1|NOT NULL|0|0普通 1精华|
|is_lock|TINYINT|1|NOT NULL|0|0开放 1锁定（禁止回复）|
|last_reply_user_id|BIGINT|20|YES|NULL|最后回复人ID|
|last_reply_time|DATETIME|-|YES|NULL|最后回复时间|
|status|TINYINT|1|NOT NULL|1|0删除 1正常|
|created_at|DATETIME|-|NOT NULL|CURRENT_TIMESTAMP|创建时间|
|updated_at|DATETIME|-|NOT NULL|CURRENT_TIMESTAMP ON UPDATE|更新时间|
|索引|-|-|-|-|forum_id + created_at（联合索引）、user_id（普通索引）、is_top（普通索引）、last_reply_time（普通索引）|
### 4.5 回复（评论）表 (bbs_reply)

|字段名|类型|长度|允许空|默认值|说明|
|---|---|---|---|---|---|
|id|BIGINT|20|NOT NULL|AUTO_INCREMENT|回复ID（主键）|
|topic_id|BIGINT|20|NOT NULL|-|所属帖子ID|
|user_id|BIGINT|20|NOT NULL|-|回复者ID|
|content|TEXT|-|NOT NULL|-|回复内容|
|parent_reply_id|BIGINT|20|YES|0|父回复ID（0为一级评论，楼中楼两级结构）|
|reply_to_user_id|BIGINT|20|YES|NULL|回复目标用户ID（楼中楼@用户）|
|like_count|INT|11|NOT NULL|0|点赞数|
|status|TINYINT|1|NOT NULL|1|0删除 1正常|
|created_at|DATETIME|-|NOT NULL|CURRENT_TIMESTAMP|创建时间|
|updated_at|DATETIME|-|NOT NULL|CURRENT_TIMESTAMP ON UPDATE|更新时间|
|索引|-|-|-|-|topic_id + created_at（联合索引）、user_id（普通索引）|
### 4.6 点赞表 (bbs_like)

|字段名|类型|长度|允许空|默认值|说明|
|---|---|---|---|---|---|
|id|BIGINT|20|NOT NULL|AUTO_INCREMENT|主键ID|
|user_id|BIGINT|20|NOT NULL|-|点赞用户ID|
|target_type|TINYINT|1|NOT NULL|-|1帖子 2回复|
|target_id|BIGINT|20|NOT NULL|-|帖子/回复ID|
|created_at|DATETIME|-|NOT NULL|CURRENT_TIMESTAMP|点赞时间|
|索引|-|-|-|-|(user_id, target_type, target_id)（联合唯一索引）|
### 4.7 收藏表 (bbs_favorite)

|字段名|类型|长度|允许空|默认值|说明|
|---|---|---|---|---|---|
|id|BIGINT|20|NOT NULL|AUTO_INCREMENT|主键ID|
|user_id|BIGINT|20|NOT NULL|-|收藏用户ID|
|post_id|BIGINT|20|NOT NULL|-|收藏帖子ID|
|created_at|DATETIME|-|NOT NULL|CURRENT_TIMESTAMP|收藏时间|
|索引|-|-|-|-|(user_id, post_id)（联合唯一索引）|
### 4.8 消息表 (bbs_message)

|字段名|类型|长度|允许空|默认值|说明|
|---|---|---|---|---|---|
|id|BIGINT|20|NOT NULL|AUTO_INCREMENT|主键ID|
|from_user_id|BIGINT|20|YES|NULL|发送者ID（NULL为系统通知）|
|to_user_id|BIGINT|20|NOT NULL|-|接收者ID|
|type|TINYINT|1|NOT NULL|1|1系统通知 2@提醒 3回复提醒 4私信|
|title|VARCHAR|100|NOT NULL|-|消息标题|
|content|VARCHAR|500|NOT NULL|-|消息内容|
|is_read|TINYINT|1|NOT NULL|0|0未读 1已读|
|created_at|DATETIME|-|NOT NULL|CURRENT_TIMESTAMP|创建时间|
|索引|-|-|-|-|to_user_id + is_read + created_at（联合索引）|
### 4.9 积分记录表 (bbs_points_log)

|字段名|类型|长度|允许空|默认值|说明|
|---|---|---|---|---|---|
|id|BIGINT|20|NOT NULL|AUTO_INCREMENT|主键ID|
|user_id|BIGINT|20|NOT NULL|-|积分变动用户ID|
|points_change|INT|11|NOT NULL|-|积分变动值（正增负减）|
|action_type|TINYINT|2|NOT NULL|-|1发帖 2回帖 3加精 4置顶 5签到 6点赞（被赞）|
|description|VARCHAR|255|YES|NULL|变动描述（如“发帖+5积分”）|
|created_at|DATETIME|-|NOT NULL|CURRENT_TIMESTAMP|变动时间|
|索引|-|-|-|-|user_id + created_at（联合索引）|
### 4.10 权限相关表（RBAC）

#### 4.10.1 角色表 (bbs_role)

|字段名|类型|长度|允许空|默认值|说明|
|---|---|---|---|---|---|
|id|BIGINT|20|NOT NULL|AUTO_INCREMENT|角色ID|
|role_name|VARCHAR|32|NOT NULL|-|角色名称（如“超级管理员”）|
|role_code|VARCHAR|32|NOT NULL|-|角色编码（如“admin”）|
|status|TINYINT|1|NOT NULL|1|0禁用 1正常|
|created_at|DATETIME|-|NOT NULL|CURRENT_TIMESTAMP|创建时间|
#### 4.10.2 权限表 (bbs_permission)

|字段名|类型|长度|允许空|默认值|说明|
|---|---|---|---|---|---|
|id|BIGINT|20|NOT NULL|AUTO_INCREMENT|权限ID|
|perm_name|VARCHAR|32|NOT NULL|-|权限名称（如“帖子置顶”）|
|perm_code|VARCHAR|32|NOT NULL|-|权限编码（如“topic:top”）|
|perm_type|TINYINT|1|NOT NULL|1|1菜单 2按钮 3接口|
|created_at|DATETIME|-|NOT NULL|CURRENT_TIMESTAMP|创建时间|
#### 4.10.3 用户-角色关联表 (bbs_user_role)

|字段名|类型|长度|允许空|默认值|说明|
|---|---|---|---|---|---|
|id|BIGINT|20|NOT NULL|AUTO_INCREMENT|主键ID|
|user_id|BIGINT|20|NOT NULL|-|用户ID|
|role_id|BIGINT|20|NOT NULL|-|角色ID|
|索引|-|-|-|-|(user_id, role_id)（联合唯一索引）|
#### 4.10.4 角色-权限关联表 (bbs_role_permission)

|字段名|类型|长度|允许空|默认值|说明|
|---|---|---|---|---|---|
|id|BIGINT|20|NOT NULL|AUTO_INCREMENT|主键ID|
|role_id|BIGINT|20|NOT NULL|-|角色ID|
|perm_id|BIGINT|20|NOT NULL|-|权限ID|
|索引|-|-|-|-|(role_id, perm_id)（联合唯一索引）|
## 五、核心接口设计（REST风格）

### 5.1 公开接口（无需登录）

|方法|路径|说明|请求参数|响应体|
|---|---|---|---|---|
|POST|/api/register|用户注册|username, password, email, nickname|注册结果 + 用户ID|
|POST|/api/login|用户登录|username/password 或 email/password|Token/Session + 用户基本信息|
|GET|/api/forums|版块列表|-|所有开放版块（树形结构）|
|GET|/api/forums/{forumId}/topics|版块帖子列表|pageNum, pageSize, sort（最新/最热）|分页帖子列表（含基础信息）|
|GET|/api/topics/{topicId}|帖子详情|-|帖子完整信息 + 分页回复列表|
|GET|/api/topics/hot|热门帖子列表|pageNum, pageSize|按热度排序的帖子列表|
### 5.2 登录后接口（需鉴权）

|方法|路径|说明|请求参数|响应体|
|---|---|---|---|---|
|POST|/api/topics|发布帖子|forumId, title, content|帖子ID + 创建结果|
|PUT|/api/topics/{topicId}|编辑帖子|title, content|更新结果|
|DELETE|/api/topics/{topicId}|删除帖子|-|删除结果|
|POST|/api/topics/{topicId}/replies|回复帖子|content, parentReplyId（可选）, replyToUserId（可选）|回复ID + 创建结果|
|POST|/api/like|点赞/取消点赞|targetType, targetId|操作结果（点赞/取消）|
|POST|/api/favorite|收藏/取消收藏|postId|操作结果（收藏/取消）|
|GET|/api/messages|我的消息列表|pageNum, pageSize, type（可选）|分页消息列表|
|PUT|/api/messages/{messageId}/read|标记消息已读|-|更新结果|
|GET|/api/user/profile|个人资料|-|用户完整资料|
|PUT|/api/user/profile|修改资料|nickname, avatar, email（可选）|更新结果|
|POST|/api/user/sign|每日签到|-|签到结果 + 积分变动|
### 5.3 管理接口（需管理员/版主权限）

|方法|路径|说明|请求参数|响应体|
|---|---|---|---|---|
|PUT|/api/admin/topics/{topicId}/top|设置置顶|topType（1全局/2版块）|更新结果|
|PUT|/api/admin/topics/{topicId}/essence|设置精华|isEssence（0/1）|更新结果|
|PUT|/api/admin/topics/{topicId}/lock|锁定/解锁|isLock（0/1）|更新结果|
|POST|/api/admin/forums|创建版块|name, description, parentId, sortOrder|版块ID + 创建结果|
|PUT|/api/admin/forums/{forumId}|编辑版块|name, description, sortOrder, status|更新结果|
|POST|/api/admin/moderators|任命版主|userId, forumId|操作结果|
|DELETE|/api/admin/moderators|撤销版主|userId, forumId|操作结果|
|PUT|/api/admin/users/{userId}/status|禁用/启用用户|status（0/1）|操作结果|
## 六、关键技术设计

### 6.1 热帖排序算法

核心公式（参考Reddit/Hacker News）：

```Plain Text

score = log(max(1, view_count)) + like_count * 2 + reply_count * 3 + time_decay
```

- time_decay：时间衰减因子（发布时间越久，分值越低，如 `1/(当前时间-创建时间+1)`）

- 定时任务：每10分钟重新计算热门帖子分值，存入Redis

### 6.2 缓存设计（Redis）

#### 6.2.1 缓存内容

- 热门帖子列表（key：`hot:topics`，TTL：10分钟）

- 帖子详情（key：`topic:{topicId}`，TTL：1小时，更新时主动失效）

- 用户信息（key：`user:{userId}`，TTL：24小时，修改时主动失效）

- 计数缓存：帖子浏览数/点赞数/回复数（key：`counter:topic:{topicId}:views`，实时更新）

#### 6.2.2 缓存策略

- Cache Aside：读时先查缓存，无则查库并回写缓存；写时先更库，再删缓存（避免脏数据）

- 深分页优化：游标分页（基于last_reply_time + id），替代limit + offset

### 6.3 防刷与安全设计

- 限流：IP/用户维度限流（如发帖≤5条/小时，回复≤20条/小时），使用Redis计数器实现

- 防XSS：输出HTML时转义，或使用Jsoup过滤富文本内容

- 防CSRF：Spring Security CSRF Token 或自定义Token校验

- 防SQL注入：MyBatis使用`#{}`参数绑定，避免拼接SQL

- 敏感词过滤：接入DFA算法，发布/回复时同步/异步过滤敏感词

- 验证码：注册/登录/高频发帖时触发图形验证码/短信验证码

### 6.4 异步化设计（Kafka/MQ）

- 消息通知：回复/点赞/@提醒等场景，异步发送消息（避免阻塞主流程）

- 积分计算：发帖/回帖/签到等积分变动，异步更新（提高接口响应速度）

- 搜索同步：帖子发布/编辑后，异步同步数据到Elasticsearch

- 计数更新：浏览数/回复数等非核心计数，异步批量更新到MySQL（减少写库次数）

### 6.5 权限控制（RBAC）

- 普通用户：仅操作自己的帖子/回复，可浏览公开内容

- 版主：管理所属版块的帖子（置顶/精华/锁定/删除），任命/撤销需管理员权限

- 超级管理员：全站权限（管理版块/用户/角色/权限）

## 七、性能优化

### 7.1 数据库优化

- 索引：核心表已配置联合索引/唯一索引，避免全表扫描

- 分页：深分页使用游标（如`where id > lastId and forumId = ? limit 20`）

- 冗余字段：帖子表冗余`last_reply_user_id`，避免联表查询最后回复人信息

- 分库分表：后期数据量大时，可按`forumId`分表（帖子/回复表）

### 7.2 应用层优化

- 延迟加载：非核心字段（如帖子正文）可按需加载

- 批量操作：批量更新计数、批量发送消息，减少IO次数

- 线程池：异步任务使用自定义线程池，控制并发数

### 7.3 高并发适配

- 读多写少：Redis缓存承接80%以上读请求（热门帖子/用户信息/计数）

- 写操作：异步化+批量处理，降低数据库写入压力

- 热点数据：热门帖子单独缓存，避免缓存击穿（布隆过滤器/互斥锁）

## 八、扩展路径（单体→微服务）

1. 第一阶段：拆分用户服务（用户注册/登录/权限），独立部署

2. 第二阶段：拆分帖子服务（帖子/回复/点赞），独立部署

3. 第三阶段：搜索服务独立（Elasticsearch集群），通知服务独立（MQ+消费端）

4. 第四阶段：基础设施层统一（分布式缓存/分布式事务/注册中心）

## 九、核心业务规则

1. 积分规则：发帖+5，回帖+2，帖子加精+10，每日签到+1，被点赞（每10次）+1

2. 置顶优先级：全局置顶 > 版块置顶 > 普通帖，按置顶时间倒排

3. 回复规则：帖子锁定后禁止新增回复，已删除回复不展示但保留记录

4. 消息规则：回复帖子时通知楼主（非本人），@用户时解析用户名并通知目标用户

5. 内容规则：Markdown/HTML双支持，图片上传对接OSS（避免存储在应用服务器）

## 十、难点总结

1. 评论结构设计：采用两级楼中楼，避免无限嵌套导致的性能问题

2. 热帖排序：平衡浏览/点赞/回复/时间维度，保证排序公平性

3. 缓存一致性：Cache Aside策略+主动失效，避免脏数据

4. 高并发读写：缓存抗压+异步写库，兼顾性能与数据一致性

5. 搜索同步：保证MySQL与Elasticsearch数据最终一致

6. 防刷与风控：多维度限流+验证码，避免恶意攻击
> （注：文档部分内容可能由 AI 生成）