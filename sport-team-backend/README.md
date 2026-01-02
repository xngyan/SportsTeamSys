# 运动组队系统 - 后端项目

Spring Boot 2.7 + MySQL 构建的运动组队系统后端 API。

## 项目结构

```
src/main/java/com/sports/
├── controller/            # REST API 控制器
├── service/              # 业务逻辑层
│   └── impl/            # 服务实现
├── repository/           # 数据访问层
├── entity/              # 数据库实体类
├── dto/                 # 数据传输对象
├── util/                # 工具类
├── interceptor/         # 拦截器
├── config/              # 配置类
└── SportTeamApplication.java   # 应用入口

resources/
├── application.yml      # 应用配置
└── init.sql            # 数据库初始化脚本
```

## 快速开始

### 前置条件
- JDK 8+
- MySQL 5.7+
- Maven 3.6+

### 安装步骤

1. **创建数据库**
```sql
CREATE DATABASE sport_team DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. **执行初始化脚本**
   - 打开 `src/main/resources/init.sql`
   - 在 MySQL 中执行所有 SQL 语句

3. **修改数据库配置**
   - 编辑 `src/main/resources/application.yml`
   - 修改数据库 URL、用户名、密码为你的实际配置

4. **构建项目**
```bash
mvn clean package
```

5. **运行项目**
```bash
mvn spring-boot:run
```

或者

```bash
java -jar target/sport-team-backend-1.0.0.jar
```

应用将在 `http://localhost:8080/api` 启动

## API 接口文档

### 认证相关

#### 用户注册
- **POST** `/api/auth/register`
- 请求体:
```json
{
  "username": "用户名",
  "password": "密码",
  "confirmPassword": "确认密码",
  "phoneNum": "电话号码（可选）",
  "stuId": "学号（可选）"
}
```

#### 用户登录
- **POST** `/api/auth/login`
- 请求体:
```json
{
  "username": "用户名",
  "password": "密码"
}
```
- 响应:
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "JWT_TOKEN",
    "user": {
      "id": 1,
      "username": "用户名",
      "avatar": "头像URL",
      "level": 1
    }
  }
}
```

### 组队相关

#### 获取组队列表（搜索）
- **GET** `/api/activities`
- 查询参数:
  - `keyword` - 搜索关键词（可选）
  - `sportType` - 运动类型（可选）
  - `location` - 地点（可选）
  - `status` - 状态（可选）: 1=招募中, 2=已满人, 3=已截止, 4=已完成
  - `page` - 页码（默认0）
  - `size` - 每页数量（默认10）

#### 创建组队
- **POST** `/api/activities`
- 需要登录（需要 Authorization Header: Bearer TOKEN）
- 请求体:
```json
{
  "title": "组队标题",
  "sportType": "篮球",
  "spotId": 1,
  "maxParticipants": 10,
  "registrationDdl": "2024-12-31T18:00:00",
  "startAt": "2024-12-31T19:00:00",
  "endAt": "2024-12-31T21:00:00",
  "description": "组队描述"
}
```

#### 获取组队详情
- **GET** `/api/activities/{id}`

#### 更新组队
- **PUT** `/api/activities/{id}`
- 需要登录且为发布者

#### 删除组队
- **DELETE** `/api/activities/{id}`
- 需要登录且为发布者

#### 获取我的组队
- **GET** `/api/activities/my-activities`
- 需要登录

### 报名相关

#### 提交报名
- **POST** `/api/registrations`
- 需要登录
- 查询参数:
  - `activityId` - 组队ID

#### 获取我的报名
- **GET** `/api/registrations/my-registrations`
- 需要登录

#### 获取活动报名列表
- **GET** `/api/registrations/activity/{activityId}`
- 需要登录且为组队发布者

#### 通过报名
- **PUT** `/api/registrations/{id}/approve`
- 需要登录且为组队发布者

#### 驳回报名
- **PUT** `/api/registrations/{id}/reject`
- 需要登录且为组队发布者

#### 取消报名
- **PUT** `/api/registrations/{id}/cancel`
- 需要登录且为报名者

### 用户相关

#### 获取用户信息
- **GET** `/api/users/{id}`

#### 获取个人资料
- **GET** `/api/users/profile`
- 需要登录

#### 更新个人资料
- **PUT** `/api/users/profile`
- 需要登录
- 查询参数:
  - `nickname` - 昵称（可选）
  - `phoneNum` - 电话（可选）
  - `avatar` - 头像URL（可选）
  - `gender` - 性别（可选）

## 数据库表结构

### users 表 - 用户信息
- `id` - 用户ID（主键）
- `username` - 用户名
- `password` - 密码哈希
- `avatar` - 头像URL
- `stu_id` - 学号（唯一）
- `phone_num` - 电话（唯一）
- `level` - 用户等级（默认1）
- `gender` - 性别（MALE/FEMALE/OTHER）
- `create_at` - 创建时间
- `update_at` - 更新时间

### spots 表 - 赛点信息
- `id` - 赛点ID（主键）
- `address` - 详细地址
- `status` - 状态（1=开放, 0=维护中）

### activities 表 - 组队信息
- `id` - 组队ID（主键）
- `creator_id` - 发布者ID（外键）
- `spot_id` - 赛点ID（外键）
- `sport_type` - 运动类型
- `title` - 组队标题
- `description` - 描述
- `max_participants` - 最大参与人数
- `min_level_required` - 最低等级要求
- `registration_ddl` - 报名截止时间
- `start_at` - 活动开始时间
- `end_at` - 活动结束时间
- `status` - 状态（1=招募中, 2=已满人, 3=已截止, 4=已完成, 0=已取消）
- `update_at` - 更新时间

### registrations 表 - 报名信息
- `id` - 报名ID（主键）
- `user_id` - 用户ID（外键）
- `activity_id` - 组队ID（外键）
- `status` - 状态（1=待审核, 2=已通过, 3=已驳回, 4=已取消）
- `registration_at` - 报名时间
- `cancel_at` - 取消时间

## 技术栈

- Spring Boot 2.7
- Spring Data JPA
- MySQL Connector
- Lombok
- JWT (JSON Web Token)
- BCrypt (密码加密)

## 配置说明

### application.yml 关键配置

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/sport_team
    username: root
    password: root
  jpa:
    hibernate:
      ddl-auto: update  # 自动创建/更新表

app:
  jwt:
    secret: your-secret-key  # JWT密钥（生产环境应修改）
    expiration: 86400000     # Token过期时间（毫秒）
```

## 演示账号

- 用户名: `admin`
- 密码: `password`

## 常见问题

### 1. 连接数据库失败
- 检查 MySQL 是否运行
- 验证数据库 URL、用户名、密码是否正确
- 确保数据库已创建

### 2. 启动时出现 Hibernate 错误
- 确保执行了 init.sql 初始化脚本
- 或者设置 `ddl-auto: create` 让 Hibernate 自动创建表

### 3. JWT Token 过期
- 前端需要处理 401 错误并重新登录
- Token 默认有效期为 24 小时

## 部署建议

1. **生产环境配置**
   - 修改 `app.jwt.secret` 为强密钥
   - 修改数据库密码
   - 设置 `ddl-auto: validate` 防止表被自动修改

2. **性能优化**
   - 配置数据库连接池
   - 添加缓存层（Redis）
   - 配置日志级别为 INFO

3. **安全加固**
   - 启用 HTTPS
   - 配置 CORS 跨域策略
   - 添加请求速率限制
   - 实现更严格的权限验证

## 许可证

MIT License
