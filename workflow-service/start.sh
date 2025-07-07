#!/bin/bash

echo "=== Workflow Service Startup Script ==="
echo "基于Camunda BPMN引擎的工作流服务启动脚本"
echo ""

# 检查Docker是否安装
if ! command -v docker &> /dev/null; then
    echo "错误: Docker未安装，请先安装Docker"
    exit 1
fi

# 检查Docker Compose是否安装
if ! command -v docker-compose &> /dev/null; then
    echo "错误: Docker Compose未安装，请先安装Docker Compose"
    exit 1
fi

echo "正在启动工作流服务..."
echo ""

# 创建uploads目录
mkdir -p uploads

# 启动服务
docker-compose up -d

echo "等待服务启动..."
sleep 30

# 检查服务状态
echo ""
echo "=== 服务状态 ==="
docker-compose ps

echo ""
echo "=== 服务已启动 ==="
echo ""
echo "访问地址:"
echo "- 工作流服务: http://localhost:8080/workflow-service"
echo "- Camunda Webapp: http://localhost:8080/workflow-service/app"
echo "- H2 Console: http://localhost:8080/workflow-service/h2-console"
echo "- REST API: http://localhost:8080/workflow-service/api/workflow"
echo "- phpMyAdmin: http://localhost:8081"
echo ""
echo "默认登录信息:"
echo "- Camunda: admin/admin123"
echo "- phpMyAdmin: root/root123"
echo ""
echo "API测试:"
echo "cd examples && chmod +x api-examples.sh && ./api-examples.sh"
echo ""
echo "停止服务:"
echo "docker-compose down"
echo ""
echo "查看日志:"
echo "docker-compose logs -f workflow-service"