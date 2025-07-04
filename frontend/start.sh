#!/bin/bash

echo "🚀 启动用户管理系统..."
echo ""

# 检查 Node.js 是否已安装
if ! command -v node &> /dev/null; then
    echo "❌ Node.js 未安装，请先安装 Node.js (18+)"
    exit 1
fi

# 检查 npm 是否已安装
if ! command -v npm &> /dev/null; then
    echo "❌ npm 未安装，请先安装 npm"
    exit 1
fi

echo "✅ 检测到 Node.js 版本: $(node --version)"
echo "✅ 检测到 npm 版本: $(npm --version)"
echo ""

# 检查是否已安装依赖
if [ ! -d "node_modules" ]; then
    echo "📦 正在安装依赖..."
    npm install
    echo "✅ 依赖安装完成"
    echo ""
fi

echo "🔧 启动开发服务器..."
echo "📍 应用将在 http://localhost:4200 启动"
echo "🎯 默认页面: 用户管理系统"
echo "⚡ 支持热重载，修改代码后自动刷新"
echo ""
echo "按 Ctrl+C 停止服务器"
echo "----------------------------------------"

# 启动开发服务器
npm start