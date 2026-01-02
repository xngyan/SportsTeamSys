# 运动组队系统 - 前端项目

Vue 3 + Vite + Element Plus 构建的运动组队系统前端应用。

## 项目结构

```
src/
├── api/                    # API 接口调用
├── assets/
│   └── styles/            # 全局样式
├── components/            # 可复用组件
├── layouts/               # 布局组件
├── pages/                 # 页面组件
├── router/                # 路由配置
├── stores/                # 状态管理 (Pinia)
├── utils/                 # 工具函数
├── App.vue               # 根组件
└── main.js               # 应用入口
```

## 快速开始

### 安装依赖
```bash
npm install
```

### 开发模式
```bash
npm run dev
```

应用将在 `http://localhost:5173` 打开

### 构建生产版本
```bash
npm run build
```

### 预览生产版本
```bash
npm run preview
```

## 功能模块

### 1. 登录注册
- 用户注册与登录
- Token 自动化管理
- 权限验证拦截

### 2. 组队广场
- 搜索和筛选组队信息
- 发布新的组队信息
- 立即报名
- 分页加载

### 3. 组队历史
- 我发布的组队列表
- 我报名的组队列表
- 组队编辑删除
- 报名取消

### 4. 个人中心
- 基础信息管理
- 密码修改
- 系统设置
- 数据统计

## 技术栈

- Vue 3
- Vite
- Vue Router 4
- Pinia (状态管理)
- Axios (HTTP 请求)
- Element Plus (UI 组件库)

## 注意事项

1. 后端 API 服务需要在 `http://localhost:8080` 运行
2. 确保后端已初始化数据库和测试数据
3. 登录时使用演示账号: `admin / password`
