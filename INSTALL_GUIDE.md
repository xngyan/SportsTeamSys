# 运动组队系统 - 完整项目指南

## 项目概述

这是一个前后端分离的运动组队系统，用户可以发布体育活动组队信息，也可以报名参加其他用户发布的组队活动。系统包含完整的用户认证、权限管理、报名审核等功能。

## 目录结构

```
sport-team-system/
├── sport-team-backend/          # 后端项目 (Spring Boot)
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/sports/
│   │   │   │   ├── controller/   # REST API 控制器
│   │   │   │   ├── service/      # 业务逻辑
│   │   │   │   ├── repository/   # 数据访问
│   │   │   │   ├── entity/       # 数据库实体
│   │   │   │   ├── dto/          # 数据传输对象
│   │   │   │   ├── util/         # 工具类
│   │   │   ├── resources/
│   │   │   │   ├── application.yml
│   │   │   │   └── init.sql     # 数据库初始化脚本
│   │   └── test/
│   ├── pom.xml                   # Maven 依赖配置
│   └── README.md
│
└── sport-team-frontend/         # 前端项目 (Vue 3)
    ├── src/
    │   ├── api/                 # API 接口调用
    │   ├── assets/              # 静态资源和样式
    │   ├── components/          # 可复用组件
    │   ├── layouts/             # 布局组件
    │   ├── pages/               # 页面组件
    │   ├── router/              # 路由配置
    │   ├── stores/              # Pinia 状态管理
    │   ├── utils/               # 工具函数
    │   ├── App.vue              # 根组件
    │   └── main.js              # 应用入口
    ├── index.html               # HTML 入口
    ├── package.json             # NPM 依赖
    ├── vite.config.js           # Vite 配置
    ├── tsconfig.json            # TypeScript 配置
    └── README.md
```

## 一、后端项目安装和运行

### 1. 环境要求
- JDK 8 或更高版本
- MySQL 5.7 或更高版本
- Maven 3.6 或更高版本

### 2. 数据库初始化

```bash
# 1. 使用 MySQL 客户端连接到 MySQL 服务器
mysql -u root -p

# 2. 执行初始化脚本
source /path/to/sport-team-backend/src/main/resources/init.sql

# 3. 验证数据库创建
USE sport_team;
SHOW TABLES;
```

初始化脚本会创建以下表：
- `users` - 用户表
- `spots` - 赛点表
- `activities` - 活动表
- `registrations` - 报名表

### 3. 配置数据库连接

编辑 `sport-team-backend/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/sport_team?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    username: root              # 修改为你的 MySQL 用户名
    password: root              # 修改为你的 MySQL 密码
    driver-class-name: com.mysql.cj.jdbc.Driver
```

### 4. 构建和运行

```bash
# 进入后端项目目录
cd sport-team-backend

# 使用 Maven 编译和运行
mvn clean install
mvn spring-boot:run

# 或者先编译成 JAR，然后运行
mvn clean package
java -jar target/sport-team-backend-1.0.0.jar
```

后端服务将在 `http://localhost:8080/api` 启动。

### 5. 测试后端 API

可以使用 Postman 或 curl 测试 API：

```bash
# 登录测试
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password"}'
```

演示账号：
- 用户名：`admin`
- 密码：`password`

## 二、前端项目安装和运行

### 1. 环境要求
- Node.js 14 或更高版本
- npm 6 或更高版本（或 yarn）

### 2. 安装依赖

```bash
# 进入前端项目目录
cd sport-team-frontend

# 使用 npm 安装依赖
npm install

# 或使用 yarn
yarn install
```

### 3. 配置后端 API 地址

前端已在 `vite.config.js` 中配置了代理：

```javascript
proxy: {
  '/api': {
    target: 'http://localhost:8080',
    changeOrigin: true,
  }
}
```

确保后端服务运行在 `http://localhost:8080`

### 4. 开发模式运行

```bash
npm run dev
```

前端应用将在 `http://localhost:5173` 启动，支持热重载。

### 5. 生产构建

```bash
# 构建优化版本
npm run build

# 预览构建结果
npm run preview
```

## 三、核心功能模块

### 1. 认证与授权
- **注册**：新用户可以注册账号
- **登录**：使用用户名和密码登录
- **Token 管理**：使用 JWT Token 进行身份验证
- **权限拦截**：需要登录的页面自动跳转到登录页

### 2. 组队广场（首页）
- **搜索功能**：按关键词、运动项目、招募状态搜索
- **发布组队**：填写表单发布新的组队信息
- **立即报名**：对感兴趣的组队提交报名申请
- **详情查看**：查看组队完整信息和报名人员

### 3. 组队历史（我的组队）
- **我发布的组队**：
  - 显示所有发布过的组队
  - 可以编辑和删除未截止的组队
  - 查看和审核报名申请（通过/驳回）
  
- **我报名的组队**：
  - 显示所有报名过的组队
  - 显示报名状态（待审核、已通过、已驳回）
  - 可以取消待审核的报名

### 4. 个人中心（我的信息）
- **基础信息管理**：修改昵称、学号、电话、性别、头像
- **我的队伍**：快速入口跳转到发布和报名列表
- **系统设置**：
  - 修改密码
  - 消息通知设置
  
- **数据统计**：
  - 发布组队数量
  - 参与报名数量
  - 通过审核数量

## 四、数据库设计

### users 表
```sql
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    avatar VARCHAR(255),
    stu_id VARCHAR(20) UNIQUE,
    phone_num VARCHAR(20) UNIQUE,
    level INT DEFAULT 1,
    gender ENUM('MALE', 'FEMALE', 'OTHER') DEFAULT 'OTHER',
    create_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### activities 表
```sql
CREATE TABLE activities (
    id INT AUTO_INCREMENT PRIMARY KEY,
    creator_id INT NOT NULL,
    spot_id INT NOT NULL,
    sport_type VARCHAR(50) NOT NULL,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    max_participants INT NOT NULL,
    min_level_required INT DEFAULT 1,
    registration_ddl DATETIME NOT NULL,
    start_at DATETIME NOT NULL,
    end_at DATETIME NOT NULL,
    status TINYINT DEFAULT 1,
    update_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (creator_id) REFERENCES users(id),
    FOREIGN KEY (spot_id) REFERENCES spots(id)
);
```

### registrations 表
```sql
CREATE TABLE registrations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    activity_id INT NOT NULL,
    status TINYINT DEFAULT 1,
    registration_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    cancel_at DATETIME,
    UNIQUE KEY uk_user_activity (user_id, activity_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (activity_id) REFERENCES activities(id)
);
```

## 五、API 接口汇总

### 认证接口
| 方法 | 端点 | 描述 |
|------|------|------|
| POST | `/api/auth/register` | 用户注册 |
| POST | `/api/auth/login` | 用户登录 |
| GET | `/api/auth/user-info` | 获取当前用户信息 |

### 组队接口
| 方法 | 端点 | 描述 |
|------|------|------|
| GET | `/api/activities` | 搜索组队列表 |
| POST | `/api/activities` | 创建组队 |
| GET | `/api/activities/{id}` | 获取组队详情 |
| PUT | `/api/activities/{id}` | 更新组队 |
| DELETE | `/api/activities/{id}` | 删除组队 |
| GET | `/api/activities/my-activities` | 获取我的组队 |

### 报名接口
| 方法 | 端点 | 描述 |
|------|------|------|
| POST | `/api/registrations` | 提交报名 |
| GET | `/api/registrations/my-registrations` | 获取我的报名 |
| GET | `/api/registrations/activity/{id}` | 获取活动报名列表 |
| PUT | `/api/registrations/{id}/approve` | 通过报名 |
| PUT | `/api/registrations/{id}/reject` | 驳回报名 |
| PUT | `/api/registrations/{id}/cancel` | 取消报名 |

### 用户接口
| 方法 | 端点 | 描述 |
|------|------|------|
| GET | `/api/users/{id}` | 获取用户信息 |
| GET | `/api/users/profile` | 获取个人资料 |
| PUT | `/api/users/profile` | 更新个人资料 |

## 六、状态码说明

### 组队状态 (activities.status)
- `1` - 招募中（可以报名）
- `2` - 已满人（人数已满）
- `3` - 已截止（报名截止）
- `4` - 已完成（活动已完成）
- `0` - 已取消（已取消）

### 报名状态 (registrations.status)
- `1` - 待审核（等待发布者审核）
- `2` - 已通过（已加入组队）
- `3` - 已驳回（报名被拒绝）
- `4` - 已取消（已取消报名）

## 七、常见问题排查

### Q1: 后端无法连接数据库
**原因**: 数据库配置不正确或 MySQL 未运行
**解决**:
1. 确保 MySQL 服务运行：`service mysql start`
2. 检查 application.yml 中的数据库 URL、用户名、密码
3. 确保数据库已创建：`CREATE DATABASE sport_team;`

### Q2: 前端无法调用后端 API
**原因**: 跨域问题或后端未启动
**解决**:
1. 确保后端在 `http://localhost:8080` 运行
2. 检查网络连接和防火墙设置
3. 查看浏览器控制台是否有 CORS 错误

### Q3: 登录后页面显示空白
**原因**: Token 验证失败或 API 返回错误
**解决**:
1. 清除浏览器缓存和 localStorage
2. 查看浏览器控制台错误信息
3. 确保使用正确的演示账号（admin/password）

### Q4: 组队信息无法加载
**原因**: 数据库中没有数据或查询出错
**解决**:
1. 检查数据库是否有活动数据
2. 查看后端日志中的 SQL 错误
3. 重新执行 init.sql 初始化脚本

## 八、部署建议

### 开发环境
```bash
# 后端
mvn spring-boot:run

# 前端
npm run dev
```

### 生产环境

**后端部署**:
```bash
# 构建
mvn clean package

# 运行（使用 nohup 后台运行）
nohup java -jar target/sport-team-backend-1.0.0.jar > app.log 2>&1 &

# 查看日志
tail -f app.log
```

**前端部署**:
```bash
# 构建
npm run build

# 使用 Nginx 或其他 Web 服务器部署 dist 目录
# Nginx 配置示例
server {
    listen 80;
    server_name yourdomain.com;
    
    location / {
        root /path/to/dist;
        try_files $uri $uri/ /index.html;
    }
    
    location /api {
        proxy_pass http://backend-server:8080;
    }
}
```

## 九、扩展建议

1. **功能扩展**
   - 添加评价和评分系统
   - 实现消息通知功能
   - 添加用户关注/粉丝功能
   - 实现活动报告和统计分析

2. **性能优化**
   - 添加 Redis 缓存
   - 实现数据库查询优化
   - 前端代码分割和懒加载
   - CDN 加速静态资源

3. **安全加固**
   - 实现更严格的权限控制
   - 添加审计日志
   - 实现 IP 白名单
   - 防止 SQL 注入和 XSS 攻击

4. **用户体验**
   - 添加实时通知（WebSocket）
   - 实现地图选择场地
   - 添加文件上传功能
   - 完善移动端适配

## 十、联系和支持

如有问题，请检查：
1. 文档中的常见问题解答
2. 后端项目的 README.md
3. 前端项目的 README.md
4. 浏览器控制台和后端日志

---

**祝您使用愉快！** 🏃‍♂️⚽🏀
