# 🏃 运动组队系统 - 快速启动指南

## 📋 前置条件检查

在启动应用之前，请确保你的系统中已安装以下工具：

- **Java**: JDK 8 或更高版本
  ```bash
  java -version
  ```

- **Maven**: 3.6 或更高版本（用于后端构建）
  ```bash
  mvn -version
  ```

- **Node.js**: 14 或更高版本
  ```bash
  node --version
  npm --version
  ```

- **MySQL**: 5.7 或更高版本
  ```bash
  mysql --version
  ```

## 🚀 快速启动方式

### 方式 1: 使用启动脚本（推荐）

#### Windows 系统
```bash
# 双击 start.bat 文件
# 或在命令行中运行：
start.bat
```

#### Linux/Mac 系统
```bash
# 给脚本添加执行权限
chmod +x start.sh

# 运行脚本
./start.sh
```

脚本会自动：
1. 检查 Java 和 Node.js 环境
2. 编译后端项目
3. 安装前端依赖
4. 启动后端服务（http://localhost:8080）
5. 启动前端服务（http://localhost:5173）

### 方式 2: 手动启动

#### 步骤 1: 启动后端

```bash
# 进入后端目录
cd sport-team-backend

# 第一次运行需要构建项目
mvn clean install

# 运行项目
mvn spring-boot:run
```

后端服务将在 `http://localhost:8080/api` 启动

#### 步骤 2: 启动前端

```bash
# 打开新的终端窗口，进入前端目录
cd sport-team-frontend

# 第一次运行需要安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端应用将在 `http://localhost:5173` 启动，并自动打开浏览器

## 🔑 登录信息

应用启动后，使用以下演示账号登录：

- **用户名**: `admin`
- **密码**: `password`

## 📝 首次使用注意事项

### 1. 数据库初始化

如果是第一次运行，需要初始化数据库：

```bash
# 使用 MySQL 命令行连接
mysql -u root -p

# 执行初始化脚本
source /path/to/sport-team-backend/src/main/resources/init.sql

# 或者在 MySQL GUI 工具中打开并执行 init.sql 文件
```

### 2. 修改数据库配置

如果你的 MySQL 用户名或密码与默认值不同，需要修改：

**文件**: `sport-team-backend/src/main/resources/application.yml`

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/sport_team
    username: root          # 修改为你的 MySQL 用户名
    password: root          # 修改为你的 MySQL 密码
```

### 3. Maven 或 npm 加速

如果在国内网络环境下，可以配置加速源：

**Maven** (`~/.m2/settings.xml`):
```xml
<mirrors>
  <mirror>
    <id>aliyun</id>
    <mirrorOf>central</mirrorOf>
    <url>http://maven.aliyun.com/nexus/content/groups/public</url>
  </mirror>
</mirrors>
```

**npm**:
```bash
npm config set registry https://registry.npmmirror.com
```

## 🌐 访问应用

启动完成后，在浏览器中访问：

- **前端应用**: [http://localhost:5173](http://localhost:5173)

将自动跳转到登录页面。输入演示账号后即可开始使用。

## 📱 主要功能模块

### 1. 组队广场（首页）
- 搜索和筛选组队信息
- 发布新的组队
- 立即报名
- 查看详情

### 2. 组队历史（我的组队）
- 我发布的组队列表
- 我报名的组队列表
- 报名审核管理
- 组队编辑删除

### 3. 个人中心（我的信息）
- 基础信息管理（昵称、学号、电话等）
- 系统设置（密码、通知提醒）
- 数据统计（发布数、报名数等）

## 🛠️ 常见问题排查

### Q: 后端无法启动，显示"端口 8080 已被占用"
**A**: 更改后端端口或关闭占用该端口的进程
```bash
# Linux/Mac 查看占用端口的进程
lsof -i :8080

# Windows 查看占用端口的进程
netstat -ano | findstr :8080
```

### Q: 前端访问后端 API 报 404 或跨域错误
**A**: 
1. 确保后端已启动
2. 检查 `vite.config.js` 中的代理配置
3. 清除浏览器缓存，重新刷新页面

### Q: 数据库连接失败
**A**:
1. 确保 MySQL 服务正在运行
2. 检查 `application.yml` 中的数据库配置
3. 验证数据库 `sport_team` 已创建

### Q: 登录后页面显示空白或异常
**A**:
1. 检查浏览器控制台错误信息（F12）
2. 清除 localStorage: `localStorage.clear()`
3. 重新刷新页面或重新登录

### Q: npm 或 Maven 依赖安装失败
**A**:
1. 检查网络连接
2. 尝试更换源（如阿里云源）
3. 删除 `node_modules` 或 `.m2` 缓存后重试

## 📚 详细文档

- **后端文档**: [sport-team-backend/README.md](sport-team-backend/README.md)
- **前端文档**: [sport-team-frontend/README.md](sport-team-frontend/README.md)
- **安装指南**: [INSTALL_GUIDE.md](INSTALL_GUIDE.md)
- **项目总结**: [PROJECT_SUMMARY.md](PROJECT_SUMMARY.md)

## 🔗 API 接口地址

所有 API 接口的基础 URL 为: `http://localhost:8080/api`

常用接口示例:
- 登录: `POST /auth/login`
- 组队列表: `GET /activities`
- 我的组队: `GET /activities/my-activities`
- 我的报名: `GET /registrations/my-registrations`

## 📞 技术支持

如遇到任何问题，请检查：
1. 相关的 README.md 文件
2. 项目中的注释和代码说明
3. 浏览器控制台和后端日志输出

## 🎉 开始使用

```bash
# 一键启动（推荐）
Windows: start.bat
Linux/Mac: ./start.sh

# 或手动启动
# 终端 1: cd sport-team-backend && mvn spring-boot:run
# 终端 2: cd sport-team-frontend && npm run dev
```

然后在浏览器中访问: **http://localhost:5173**

---

**祝您使用愉快！** 🏃‍♀️⚽🎾🏀

有任何问题，请参考项目文档或查看代码注释。
