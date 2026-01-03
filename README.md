# 🏃 运动组队系统 (Sports Team System)

这是一个基于 Spring Boot 和 Vue 3 开发的全栈运动组队平台，旨在为运动爱好者提供便捷的活动发布、组队报名和赛点管理功能。

## 📚 文档导航

本项目包含详细的文档说明，请参考以下链接获取更多信息：

- [📖 项目总结 (PROJECT_SUMMARY.md)](PROJECT_SUMMARY.md) - 详细的功能列表和完成情况
- [🚀 快速启动指南 (QUICK_START.md)](QUICK_START.md) - 环境要求和启动步骤
- [📂 项目结构说明 (PROJECT_STRUCTURE.md)](PROJECT_STRUCTURE.md) - 目录结构和代码组织
- [🔧 安装指南 (INSTALL_GUIDE.md)](INSTALL_GUIDE.md) - 详细的安装和配置说明

## ✨ 主要功能

### 核心模块
- **用户认证**：注册、登录、JWT 身份验证
- **组队广场**：浏览活动、搜索筛选、查看详情
- **活动管理**：发布活动、编辑/取消活动
- **报名系统**：参与报名、发起人审核、取消报名
- **个人中心**：个人资料管理、历史活动记录

## 🛠️ 技术栈

### 后端 (sport-team-backend)
- **框架**: Spring Boot
- **数据库**: MySQL
- **ORM**: JPA / Hibernate
- **安全**: JWT (JSON Web Token)
- **构建工具**: Maven

### 前端 (sport-team-frontend)
- **框架**: Vue 3
- **构建工具**: Vite
- **路由**: Vue Router
- **状态管理**: Pinia
- **样式**: CSS / Scoped Styles

## 🚀 快速开始

### 方式 1: 使用一键启动脚本 (推荐)

**Windows:**
直接运行根目录下的 `start.bat` 脚本。

**Linux/Mac:**
```bash
chmod +x start.sh
./start.sh
```

### 方式 2: 手动启动

请参考 [快速启动指南](QUICK_START.md) 了解详细的手动启动步骤。

## 📂 目录结构概览

```
SportsTeamSys/
├── sport-team-backend/    # 后端 Java Spring Boot 项目
├── sport-team-frontend/   # 前端 Vue 3 项目
├── start.bat              # Windows 启动脚本
├── start.sh               # Linux/Mac 启动脚本
└── *.md                   # 项目文档
```

## 📝 许可证

本项目仅供学习和交流使用。
