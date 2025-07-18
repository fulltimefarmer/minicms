#!/bin/bash

echo "🚀 启动 CMS 前端 Mock 模式"
echo "================================"

# 检查是否安装了必要的依赖
if [ ! -d "node_modules" ]; then
  echo "📦 安装依赖..."
  npm install
fi

# 生成mock数据
echo "📊 生成 Mock 数据..."
npm run mock:generate

# 启动mock模式
echo "🌐 启动应用程序 (Mock 模式)..."
echo "前端地址: http://localhost:4200"
echo "Mock API地址: http://localhost:3001"
echo "================================"
echo "默认登录账号:"
echo "用户名: admin"
echo "密码: 123456"
echo "================================"

npm run start:mock