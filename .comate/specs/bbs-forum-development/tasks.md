# BBS论坛系统开发任务清单

## 第一阶段：项目基础架构搭建

- [x] Task 1: 初始化Spring Boot 3.5.11项目
  - 1.1: 创建Maven项目结构，配置pom.xml依赖
  - 1.2: 创建application.yml主配置文件
  - 1.3: 创建启动类BBSApplication.java

- [x] Task 2: 创建数据库表SQL脚本
  - 2.1: 创建bbs_user用户表
  - 2.2: 创建bbs_forum版块表
  - 2.3: 创建bbs_moderator版主关联表
  - 2.4: 创建bbs_topic帖子表
  - 2.5: 创建bbs_reply回复表
  - 2.6: 创建bbs_like点赞表
  - 2.7: 创建bbs_favorite收藏表
  - 2.8: 创建bbs_message消息表
  - 2.9: 创建bbs_points_log积分记录表
  - 2.10: 创建RBAC权限相关表

## 第二阶段：通用组件开发

- [x] Task 3: 统一响应和异常处理
  - 3.1: 创建Result统一响应类
  - 3.2: 创建GlobalExceptionHandler全局异常处理器
  - 3.3: 创建BusinessException业务异常类

- [x] Task 4: Redis配置和工具类
  - 4.1: 创建RedisConfig配置类
  - 4.2: 创建RedisUtils工具类

- [x] Task 5: JWT认证组件
  - 5.1: 创建JwtProperties配置属性类
  - 5.2: 创建JwtUtils工具类
  - 5.3: 创建JwtInterceptor拦截器
  - 5.4: 创建WebMvcConfig注册拦截器

## 第三阶段：用户模块开发

- [x] Task 6: 用户实体类和Mapper
  - 6.1: 创建User实体类
  - 6.2: 创建UserMapper接口
  - 6.3: 创建UserMapper.xml映射文件

- [x] Task 7: 用户DTO和VO类
  - 7.1: 创建LoginRequestDTO登录请求
  - 7.2: 创建RegisterRequestDTO注册请求
  - 7.3: 创建UserVO用户视图对象
  - 7.4: 创建LoginResponseVO登录响应

- [x] Task 8: 用户Service层
  - 8.1: 创建UserService接口
  - 8.2: 创建UserServiceImpl实现类
  - 8.3: 实现注册功能
  - 8.4: 实现登录功能（JWT Token生成）
  - 8.5: 实现获取用户信息

- [x] Task 9: 用户Controller层
  - 9.1: 创建AuthController控制器
  - 9.2: 实现POST /api/register接口
  - 9.3: 实现POST /api/login接口
  - 9.4: 创建UserController控制器
  - 9.5: 实现GET /api/user/profile接口
  - 9.6: 实现PUT /api/user/profile接口
  - 9.7: 实现POST /api/user/sign签到接口

## 第四阶段：版块模块开发

- [x] Task 10: 版块实体类和Mapper
  - 10.1: 创建Forum实体类
  - 10.2: 创建ForumMapper接口和xml

- [x] Task 11: 版块Service层
  - 11.1: 创建ForumService接口
  - 11.2: 创建ForumServiceImpl实现类
  - 11.3: 实现获取版块树形列表

- [x] Task 12: 版块Controller层
  - 12.1: 创建ForumController控制器
  - 12.2: 实现GET /api/forums接口
  - 12.3: 创建AdminForumController管理控制器
  - 12.4: 实现POST /api/admin/forums接口
  - 12.5: 实现PUT /api/admin/forums/{forumId}接口

## 第五阶段：帖子模块开发

- [x] Task 13: 帖子实体类和Mapper
  - 13.1: 创建Topic实体类
  - 13.2: 创建TopicMapper接口和xml

- [x] Task 14: 帖子DTO和VO类
  - 14.1: 创建TopicCreateRequestDTO创建请求
  - 14.2: 创建TopicUpdateRequestDTO更新请求
  - 14.3: 创建TopicVO帖子视图对象

- [x] Task 15: 帖子Service层
  - 15.1: 创建TopicService接口
  - 15.2: 创建TopicServiceImpl实现类
  - 15.3: 实现创建帖子功能
  - 15.4: 实现更新帖子功能
  - 15.5: 实现删除帖子功能
  - 15.6: 实现获取帖子详情
  - 15.7: 实现版块帖子分页列表
  - 15.8: 实现置顶/加精/锁定功能

- [x] Task 16: 帖子Controller层
  - 16.1: 创建TopicController控制器
  - 16.2: 实现POST /api/topics接口
  - 16.3: 实现GET /api/topics/{topicId}接口
  - 16.4: 实现PUT /api/topics/{topicId}接口
  - 16.5: 实现DELETE /api/topics/{topicId}接口
  - 16.6: 实现GET /api/forums/{forumId}/topics接口
  - 16.7: 实现GET /api/topics/hot热门帖子接口
  - 16.8: 创建AdminTopicController管理控制器
  - 16.9: 实现PUT /api/admin/topics/{topicId}/top接口
  - 16.10: 实现PUT /api/admin/topics/{topicId}/essence接口
  - 16.11: 实现PUT /api/admin/topics/{topicId}/lock接口

## 第六阶段：评论模块开发

- [x] Task 17: 评论实体类和Mapper
  - 17.1: 创建Reply实体类
  - 17.2: 创建ReplyMapper接口和xml

- [x] Task 18: 评论DTO和VO类
  - 18.1: 创建ReplyCreateRequestDTO创建请求
  - 18.2: 创建ReplyVO评论视图对象

- [x] Task 19: 评论Service层
  - 19.1: 创建ReplyService接口
  - 19.2: 创建ReplyServiceImpl实现类
  - 19.3: 实现创建回复功能
  - 19.4: 实现获取帖子回复列表（楼中楼结构）

- [x] Task 20: 评论Controller层
  - 20.1: 创建ReplyController控制器
  - 20.2: 实现POST /api/topics/{topicId}/replies接口

## 第七阶段：互动功能开发

- [x] Task 21: 点赞功能
  - 21.1: 创建Like实体类和Mapper
  - 21.2: 创建LikeService接口和实现
  - 21.3: 实现POST /api/like接口

- [x] Task 22: 收藏功能
  - 22.1: 创建Favorite实体类和Mapper
  - 22.2: 创建FavoriteService接口和实现
  - 22.3: 实现POST /api/favorite接口

- [x] Task 23: 消息通知功能
  - 23.1: 创建Message实体类和Mapper
  - 23.2: 创建MessageService接口和实现
  - 23.3: 实现GET /api/messages接口
  - 23.4: 实现PUT /api/messages/{messageId}/read接口

## 第八阶段：积分和签到功能

- [x] Task 24: 积分系统
  - 24.1: 创建PointsLog实体类和Mapper
  - 24.2: 创建PointsService接口和实现
  - 24.3: 实现积分变动逻辑（发帖+5，回帖+2等）
  - 24.4: 实现每日签到+1积分

- [x] Task 25: 热帖排行功能
  - 25.1: 创建HotTopic定时任务
  - 25.2: 实现热帖排序算法
  - 25.3: 实现Redis缓存热门帖子

## 第九阶段：管理后台

- [x] Task 26: 版主管理
  - 26.1: 创建Moderator实体类和Mapper
  - 26.2: 实现任命版主功能
  - 26.3: 实现撤销版主功能
  - 26.4: 实现PUT /api/admin/moderators接口

- [x] Task 27: 用户管理
  - 27.1: 实现禁用/启用用户功能
  - 27.2: 实现PUT /api/admin/users/{userId}/status接口

- [x] Task 28: RBAC权限系统
  - 28.1: 创建Role、Permission实体类
  - 28.2: 创建用户角色关联表操作
  - 28.3: 实现权限拦截器

## 第十阶段：前端开发

- [x] Task 29: Vue 3项目初始化
  - 29.1: 使用Vite创建Vue 3项目
  - 29.2: 安装Element Plus、Axios等依赖
  - 29.3: 配置路由和状态管理

- [x] Task 30: 前端页面开发
  - 30.1: 开发登录/注册页面
  - 30.2: 开发首页（版块列表）
  - 30.3: 开发帖子列表页
  - 30.4: 开发帖子详情页
  - 30.5: 开发发布帖子页面
  - 30.6: 开发个人中心页面
  - 30.7: 开发消息通知页面
  - 30.8: 开发管理后台页面