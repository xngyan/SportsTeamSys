@echo off
REM 运动组队系统 - 快速启动脚本 (Windows)

echo ======================================
echo 运动组队系统 - 快速启动
echo ======================================
echo.

REM 检查 Java
java -version >nul 2>&1
if errorlevel 1 (
    echo. ❌ 未找到 Java，请先安装 JDK 8+
    pause
    exit /b 1
)

REM 检查 Node.js
node --version >nul 2>&1
if errorlevel 1 (
    echo. ❌ 未找到 Node.js，请先安装 Node.js 14+
    pause
    exit /b 1
)

echo. ✅ Java 和 Node.js 已安装
echo.

REM 1. 启动后端
echo ======================================
echo 1. 启动后端服务...
echo ======================================
cd sport-team-backend

if not exist "target" (
    echo. 📦 首次运行，正在编译项目...
    call mvn clean install
)

echo. 🚀 启动后端服务 (运行在 http://localhost:8080/api)...
start cmd /k "mvn spring-boot:run"

REM 等待后端启动
timeout /t 10 /nobreak

REM 2. 启动前端
echo.
echo ======================================
echo 2. 启动前端服务...
echo ======================================
cd ..\sport-team-frontend

if not exist "node_modules" (
    echo. 📦 首次运行，正在安装依赖...
    call npm install
)

echo. 🚀 启动前端服务 (运行在 http://localhost:5173)...
start cmd /k "npm run dev"

echo.
echo ======================================
echo ✅ 服务已启动！
echo ======================================
echo.
echo 📋 访问地址：
echo   - 前端应用: http://localhost:5173
echo   - 后端 API: http://localhost:8080/api
echo.
echo 🔑 演示账号：
echo   - 用户名: admin
echo   - 密码: password
echo.
echo 💡 提示：请勿关闭这两个命令窗口
echo.
pause
