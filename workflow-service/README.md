# Workflow Service - 基于Camunda BPMN引擎的工作流服务

## 概述

本项目是一个基于Camunda BPMN引擎的工作流服务系统，实现了员工请假申请流程和业务申请流程（包含文件上传功能）。系统支持manager审批功能，并提供完整的REST API接口。

## 功能特性

### 1. 员工请假流程
- 员工提交请假申请
- Manager审批请假申请
- 自动通知审批结果
- 请假申请状态跟踪

### 2. 业务申请流程
- 员工提交业务申请
- 支持合同文件上传
- 文档审查环节
- Manager最终审批
- 文件下载功能

### 3. 工作流管理
- 基于Camunda BPMN引擎
- 可视化流程定义
- 任务分配和认领
- 流程监控和管理

## 技术栈

- **Spring Boot 3.1.0** - 应用框架
- **Camunda BPMN Engine 7.19.0** - 工作流引擎
- **Spring Data JPA** - 数据持久化
- **H2 Database** - 嵌入式数据库
- **Spring Security** - 安全框架
- **Maven** - 项目构建工具

## 项目结构

```
workflow-service/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/company/workflow/
│   │   │       ├── WorkflowServiceApplication.java
│   │   │       ├── config/
│   │   │       │   └── SecurityConfig.java
│   │   │       ├── controller/
│   │   │       │   └── WorkflowController.java
│   │   │       ├── model/
│   │   │       │   ├── LeaveRequest.java
│   │   │       │   └── BusinessRequest.java
│   │   │       ├── repository/
│   │   │       │   ├── LeaveRequestRepository.java
│   │   │       │   └── BusinessRequestRepository.java
│   │   │       └── service/
│   │   │           ├── WorkflowService.java
│   │   │           ├── FileUploadService.java
│   │   │           └── NotificationService.java
│   │   └── resources/
│   │       ├── application.yml
│   │       └── processes/
│   │           ├── leave-request-process.bpmn
│   │           └── business-request-process.bpmn
│   └── test/
├── pom.xml
└── README.md
```

## 启动和运行

### 1. 环境要求
- Java 17+
- Maven 3.6+

### 2. 启动应用
```bash
cd workflow-service
mvn spring-boot:run
```

### 3. 访问应用
- **应用服务**: http://localhost:8080/workflow-service
- **Camunda Webapp**: http://localhost:8080/workflow-service/app
- **H2 Console**: http://localhost:8080/workflow-service/h2-console
- **REST API**: http://localhost:8080/workflow-service/api/workflow

### 4. Camunda默认登录
- 用户名: demo
- 密码: demo

## API接口文档

### 请假申请API

#### 1. 提交请假申请
```
POST /api/workflow/leave-request
Content-Type: application/json

{
    "employeeId": "EMP001",
    "employeeName": "张三",
    "leaveType": "年假",
    "startDate": "2024-01-15",
    "endDate": "2024-01-20",
    "reason": "休假",
    "managerId": "MGR001"
}
```

#### 2. 获取员工请假记录
```
GET /api/workflow/leave-request/employee/{employeeId}
```

#### 3. 获取Manager待审批请假
```
GET /api/workflow/leave-request/manager/{managerId}
```

#### 4. 审批请假申请
```
POST /api/workflow/leave-request/approve
Content-Type: application/json

{
    "taskId": "task-id-here",
    "managerId": "MGR001",
    "decision": "APPROVED", // 或 "REJECTED"
    "comment": "审批意见"
}
```

### 业务申请API

#### 1. 提交业务申请（支持文件上传）
```
POST /api/workflow/business-request
Content-Type: multipart/form-data

employeeId=EMP001
employeeName=张三
businessType=合同审批
title=供应商合同
description=描述信息
managerId=MGR001
contractFile=file.pdf
```

#### 2. 获取员工业务申请记录
```
GET /api/workflow/business-request/employee/{employeeId}
```

#### 3. 获取Manager待审批业务申请
```
GET /api/workflow/business-request/manager/{managerId}
```

#### 4. 文档审查
```
POST /api/workflow/business-request/review-document
Content-Type: application/json

{
    "taskId": "task-id-here",
    "managerId": "MGR001",
    "documentReviewed": true
}
```

#### 5. 审批业务申请
```
POST /api/workflow/business-request/approve
Content-Type: application/json

{
    "taskId": "task-id-here",
    "managerId": "MGR001",
    "decision": "APPROVED", // 或 "REJECTED"
    "comment": "审批意见"
}
```

#### 6. 下载合同文件
```
GET /api/workflow/business-request/{id}/download
```

### 任务管理API

#### 1. 获取用户任务
```
GET /api/workflow/tasks/user/{userId}
```

#### 2. 获取候选任务
```
GET /api/workflow/tasks/candidate/{userId}
```

#### 3. 获取组任务
```
GET /api/workflow/tasks/group/{groupId}
```

#### 4. 认领任务
```
POST /api/workflow/tasks/{taskId}/claim
Content-Type: application/json

{
    "userId": "user-id"
}
```

#### 5. 获取任务详情
```
GET /api/workflow/tasks/{taskId}
```

#### 6. 获取任务变量
```
GET /api/workflow/tasks/{taskId}/variables
```

## 工作流程说明

### 员工请假流程
1. 员工提交请假申请
2. 系统启动工作流实例
3. Manager收到审批任务
4. Manager审批（批准/拒绝）
5. 系统发送通知给员工
6. 流程结束

### 业务申请流程
1. 员工提交业务申请（含文件上传）
2. 系统启动工作流实例
3. Manager进行文档审查
4. 如果文档有问题，通知员工重新提交
5. 如果文档通过，进入审批环节
6. Manager最终审批（批准/拒绝）
7. 系统发送通知给员工
8. 流程结束

## 文件上传配置

默认文件上传目录: `./uploads`

可以通过配置文件修改：
```yaml
file:
  upload:
    directory: /path/to/uploads
```

支持的文件类型：所有类型
最大文件大小：10MB

## 数据库配置

### H2数据库（默认）
- 数据库文件：内存数据库
- 控制台访问：http://localhost:8080/workflow-service/h2-console
- 连接URL：jdbc:h2:mem:camunda-h2-database
- 用户名：sa
- 密码：（空）

### 切换到MySQL
修改application.yml：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/workflow_db
    username: your_username
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver
  
camunda:
  bpm:
    database:
      type: mysql
```

## 开发指南

### 添加新的工作流程
1. 创建BPMN文件放在`src/main/resources/processes/`目录
2. 创建对应的实体类和Repository
3. 在WorkflowService中添加启动流程的方法
4. 在WorkflowController中添加REST API
5. 如需要通知功能，在NotificationService中添加处理逻辑

### 自定义任务处理
实现JavaDelegate接口创建自定义任务处理器：
```java
@Component("customTaskHandler")
public class CustomTaskHandler implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        // 自定义处理逻辑
    }
}
```

## 监控和管理

### Camunda Cockpit
访问：http://localhost:8080/workflow-service/app/cockpit/
功能：
- 流程实例监控
- 任务管理
- 流程历史查看
- 性能分析

### Spring Boot Actuator
访问：http://localhost:8080/workflow-service/actuator/
功能：
- 健康检查：/actuator/health
- 应用信息：/actuator/info
- 性能指标：/actuator/metrics

## 常见问题

### 1. 启动失败
- 检查Java版本是否为17+
- 检查端口8080是否被占用
- 查看启动日志中的具体错误信息

### 2. 无法访问Camunda Webapp
- 确认应用已成功启动
- 检查URL是否正确：http://localhost:8080/workflow-service/app
- 使用默认用户名密码：demo/demo

### 3. 文件上传失败
- 检查文件大小是否超过10MB限制
- 确认uploads目录有写权限
- 查看服务器日志中的详细错误信息

## 扩展功能

### 1. 邮件通知
集成Spring Mail发送邮件通知：
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>
```

### 2. 消息队列
集成RabbitMQ或Kafka处理异步任务：
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>
```

### 3. 缓存
集成Redis缓存提高性能：
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

## 许可证

本项目遵循MIT许可证。

## 贡献

欢迎提交Issue和Pull Request来改进这个项目。