# Workflow Service 模块总结

## 项目概述

基于 Camunda BPMN 引擎的工作流服务模块，实现了完整的员工请假流程和业务申请流程（包含文件上传功能）。系统支持 manager 审批功能，并提供完整的 REST API 接口。

## 核心功能

### 1. 员工请假流程
- ✅ 员工提交请假申请
- ✅ Manager审批请假申请
- ✅ 自动通知审批结果
- ✅ 请假申请状态跟踪

### 2. 业务申请流程
- ✅ 员工提交业务申请
- ✅ 支持合同文件上传
- ✅ 文档审查环节
- ✅ Manager最终审批
- ✅ 文件下载功能

### 3. 工作流管理
- ✅ 基于Camunda BPMN引擎
- ✅ 可视化流程定义
- ✅ 任务分配和认领
- ✅ 流程监控和管理

## 技术架构

### 后端技术栈
- **Spring Boot 3.1.0** - 应用框架
- **Camunda BPMN Engine 7.19.0** - 工作流引擎
- **Spring Data JPA** - 数据持久化
- **H2/MySQL Database** - 数据库
- **Spring Security** - 安全框架
- **Maven** - 项目构建工具

### 项目结构
```
workflow-service/
├── src/main/java/com/company/workflow/
│   ├── WorkflowServiceApplication.java         # 主应用类
│   ├── config/SecurityConfig.java              # 安全配置
│   ├── controller/WorkflowController.java      # REST API控制器
│   ├── model/                                  # 数据模型
│   │   ├── LeaveRequest.java                   # 请假申请实体
│   │   └── BusinessRequest.java                # 业务申请实体
│   ├── repository/                             # 数据仓库
│   │   ├── LeaveRequestRepository.java         # 请假申请仓库
│   │   └── BusinessRequestRepository.java      # 业务申请仓库
│   └── service/                                # 服务层
│       ├── WorkflowService.java                # 工作流服务
│       ├── FileUploadService.java              # 文件上传服务
│       └── NotificationService.java            # 通知服务
├── src/main/resources/
│   ├── application.yml                         # 应用配置
│   ├── application-docker.yml                  # Docker环境配置
│   └── processes/                              # BPMN流程定义
│       ├── leave-request-process.bpmn          # 请假流程定义
│       └── business-request-process.bpmn       # 业务流程定义
├── examples/api-examples.sh                    # API使用示例
├── Dockerfile                                  # Docker构建文件
├── docker-compose.yml                          # Docker Compose配置
└── start.sh                                    # 快速启动脚本
```

## API接口

### 请假申请 API
- `POST /api/workflow/leave-request` - 提交请假申请
- `GET /api/workflow/leave-request/employee/{employeeId}` - 获取员工请假记录
- `GET /api/workflow/leave-request/manager/{managerId}` - 获取Manager待审批请假
- `POST /api/workflow/leave-request/approve` - 审批请假申请

### 业务申请 API
- `POST /api/workflow/business-request` - 提交业务申请（支持文件上传）
- `GET /api/workflow/business-request/employee/{employeeId}` - 获取员工业务申请记录
- `GET /api/workflow/business-request/manager/{managerId}` - 获取Manager待审批业务申请
- `POST /api/workflow/business-request/review-document` - 文档审查
- `POST /api/workflow/business-request/approve` - 审批业务申请
- `GET /api/workflow/business-request/{id}/download` - 下载合同文件

### 任务管理 API
- `GET /api/workflow/tasks/user/{userId}` - 获取用户任务
- `GET /api/workflow/tasks/candidate/{userId}` - 获取候选任务
- `GET /api/workflow/tasks/group/{groupId}` - 获取组任务
- `POST /api/workflow/tasks/{taskId}/claim` - 认领任务
- `GET /api/workflow/tasks/{taskId}` - 获取任务详情
- `GET /api/workflow/tasks/{taskId}/variables` - 获取任务变量

## 部署方式

### 1. 本地开发启动
```bash
cd workflow-service
mvn spring-boot:run
```

### 2. Docker 部署
```bash
# 快速启动
chmod +x start.sh
./start.sh

# 或手动启动
docker-compose up -d
```

### 3. Maven 构建
```bash
mvn clean package
java -jar target/workflow-service-1.0.0.jar
```

## 访问地址

- **应用服务**: http://localhost:8080/workflow-service
- **Camunda Webapp**: http://localhost:8080/workflow-service/app
- **H2 Console**: http://localhost:8080/workflow-service/h2-console
- **REST API**: http://localhost:8080/workflow-service/api/workflow
- **phpMyAdmin**: http://localhost:8081 (仅Docker部署)

## 默认登录信息

- **Camunda**: demo/demo (本地) 或 admin/admin123 (Docker)
- **H2 Console**: sa/（空密码）
- **phpMyAdmin**: root/root123

## 工作流程图

### 员工请假流程
```
提交请假申请 → Manager审批 → 发送通知 → 流程结束
```

### 业务申请流程
```
提交业务申请 → 文档审查 → Manager审批 → 发送通知 → 流程结束
                    ↓
                  文档问题 → 通知重新提交
```

## 测试验证

### 1. 单元测试
```bash
mvn test
```

### 2. API测试
```bash
cd examples
chmod +x api-examples.sh
./api-examples.sh
```

### 3. 功能测试
- 通过 Camunda Webapp 可视化查看流程状态
- 通过 REST API 测试各种业务场景
- 通过文件上传测试文件处理功能

## 核心特性

### 1. 工作流引擎
- 基于 Camunda BPMN 2.0 标准
- 支持复杂业务流程定义
- 提供可视化流程监控
- 支持任务分配和认领

### 2. 文件管理
- 支持多种文件类型上传
- 文件大小限制（10MB）
- 安全的文件存储和访问
- 文件下载功能

### 3. 通知系统
- 流程状态变更通知
- 审批结果通知
- 可扩展的通知机制
- 支持多种通知方式

### 4. 数据持久化
- JPA/Hibernate 数据访问
- 支持 H2 和 MySQL 数据库
- 数据库自动初始化
- 事务管理

### 5. 安全性
- Spring Security 集成
- API 访问控制
- 文件上传安全检查
- 数据库连接安全

## 扩展建议

### 1. 功能扩展
- 添加邮件通知功能
- 集成消息队列（RabbitMQ/Kafka）
- 添加缓存机制（Redis）
- 实现用户权限管理
- 添加更多业务流程模板

### 2. 性能优化
- 数据库索引优化
- 缓存机制实现
- 文件存储优化
- 异步处理机制

### 3. 监控和运维
- 集成 Prometheus 监控
- 添加日志聚合
- 性能指标收集
- 健康检查机制

## 项目亮点

1. **完整的工作流解决方案** - 基于行业标准 BPMN 2.0
2. **灵活的流程定义** - 支持复杂业务流程建模
3. **文件处理能力** - 完整的文件上传和管理功能
4. **REST API 设计** - 符合 RESTful 设计原则
5. **容器化部署** - Docker 和 Docker Compose 支持
6. **可视化管理** - Camunda Webapp 提供流程监控
7. **多环境支持** - 开发、测试、生产环境配置
8. **扩展性设计** - 易于添加新的业务流程

## 总结

这个 Workflow Service 模块成功实现了基于 Camunda BPMN 引擎的完整工作流系统，包含：

- ✅ 员工请假流程
- ✅ 业务申请流程（含文件上传）
- ✅ Manager 审批功能
- ✅ 完整的 REST API
- ✅ 可视化流程监控
- ✅ 多环境部署支持
- ✅ 完整的文档和示例

该模块可以作为企业级工作流系统的基础，支持各种复杂的业务流程需求。