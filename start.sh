#!/bin/bash

# 运动组队系统 - 快速启动脚本

echo "======================================"
echo "运动组队系统 - 快速启动"
echo "======================================"
echo ""

# 检查 Java 是否安装
if ! command -v java &> /dev/null; then
    echo "❌ 未找到 Java，请先安装 JDK 8+"
    exit 1
fi

# 检查 Node.js 是否安装
if ! command -v node &> /dev/null; then
    echo "❌ 未找到 Node.js，请先安装 Node.js 14+"
    exit 1
fi

echo "✅ Java 和 Node.js 已安装"
echo ""

# 1. 启动后端
echo "======================================"
echo "1. 启动后端服务..."
echo "======================================"
cd sport-team-backend

if [ ! -d "target" ]; then
    echo "📦 首次运行，正在编译项目..."
    mvn clean install
fi

echo "🚀 启动后端服务 (运行在 http://localhost:8080/api)..."
mvn spring-boot:run &
BACKEND_PID=$!

# 等待后端启动
sleep 10

# 2. 启动前端
echo ""
echo "======================================"
echo "2. 启动前端服务..."
echo "======================================"
cd ../sport-team-frontend

if [ ! -d "node_modules" ]; then
    echo "📦 首次运行，正在安装依赖..."
    npm install
fi

echo "🚀 启动前端服务 (运行在 http://localhost:5173)..."
npm run dev &
FRONTEND_PID=$!

echo ""
echo "======================================"
echo "✅ 服务已启动！"
echo "======================================"
echo ""
echo "📋 访问地址："
echo "  - 前端应用: http://localhost:5173"
echo "  - 后端 API: http://localhost:8080/api"
echo ""
echo "🔑 演示账号："
echo "  - 用户名: admin"
echo "  - 密码: password"
echo ""
echo "📝 按 Ctrl+C 停止服务"
echo ""

# 保持脚本运行
wait
