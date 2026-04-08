# BBS 论坛系统

基于 Spring Boot 3.5.11 + MySQL 8 + Vue 3 的前后端分离论坛系统。

## 技术栈

### 后端
- Spring Boot 3.5.11
- MyBatis-Plus 3.5.7
- MySQL 8.0
- Redis
- JWT 认证

### 前端
- Vue 3 + Vite
- Element Plus
- Pinia 状态管理
- Vue Router

## 项目结构

```
bbs/
├── bbs-backend/          # 后端项目
│   ├── src/main/java/
│   │   └── com/bbs/
│   │       ├── controller/   # 控制器
│   │       ├── service/      # 业务逻辑
│   │       ├── mapper/       # 数据访问
│   │       ├── entity/       # 实体类
│   │       ├── config/       # 配置类
│   │       ├── common/       # 公共组件
│   │       ├── interceptor/  # 拦截器
│   │       └── task/         # 定时任务
│   ├── src/main/resources/
│   │   ├── schema.sql        # 数据库表结构
│   │   └── data.sql          # 初始化数据
│   └── sql/
│       └── bbs_init.sql      # 完整初始化脚本
│
├── bbs-frontend/         # 前端项目
│   ├── src/
│   │   ├── api/             # API 接口
│   │   ├── views/           # 页面组件
│   │   ├── stores/          # 状态管理
│   │   ├── router/          # 路由配置
│   │   └── components/      # 公共组件
│   └── dist/                # 构建产物
│
└── build.ps1              # 一键打包脚本
```

## 快速开始

### 环境要求
- JDK 21+
- Node.js 18+
- MySQL 8.0+
- Redis 6.0+

### 配置数据库

修改 `bbs-backend/src/main/resources/application.yml` 中的数据库配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/bbs_db
    username: root
    password: your_password
  data:
    redis:
      password: your_redis_password
```

### 一键打包（Windows）

```powershell
.\build.ps1
```

### 手动打包

1. 安装前端依赖并构建：
```bash
cd bbs-frontend
npm install
npm run build
```

2. 复制前端到后端静态目录：
```bash
mkdir -p bbs-backend/src/main/resources/static
cp -r bbs-frontend/dist/* bbs-backend/src/main/resources/static/
```

3. 打包后端：
```bash
cd bbs-backend
mvn clean package -DskipTests
```

### 启动

```bash
java -jar bbs-backend/target/bbs-backend-1.0.0.jar
```

访问地址：http://localhost:8580

### 默认账号
- 用户名：admin
- 密码：admin123

## 功能特性

- 用户注册/登录
- 版块管理
- 帖子发布/编辑/删除
- 回复/评论
- 点赞/收藏
- 消息通知
- 积分系统
- 签到功能
- 帖子置顶/精华/锁定
- 版主管理
- RBAC 权限控制

## API 文档

### 用户接口
- `POST /api/user/register` - 用户注册
- `POST /api/user/login` - 用户登录
- `GET /api/user/info` - 获取用户信息

### 版块接口
- `GET /api/forums` - 获取版块列表
- `GET /api/forums/{id}` - 获取版块详情

### 帖子接口
- `GET /api/forums/{id}/topics` - 获取帖子列表
- `GET /api/topics/{id}` - 获取帖子详情
- `POST /api/topics` - 创建帖子
- `PUT /api/topics/{id}` - 更新帖子
- `DELETE /api/topics/{id}` - 删除帖子

### 回复接口
- `GET /api/topics/{id}/replies` - 获取回复列表
- `POST /api/replies` - 创建回复
- `DELETE /api/replies/{id}` - 删除回复

## 许可证

MIT License