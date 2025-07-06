# 员工请假管理系统

基于Activiti工作流的员工请假申请和审批管理系统。

## 功能概述

### 主要功能
- **请假申请**：员工可以创建、修改、删除和取消请假申请
- **工作流审批**：基于Activiti的多级审批流程
- **审批历史**：完整的审批历史记录和追踪
- **状态管理**：请假申请状态的完整生命周期管理
- **多种请假类型**：支持年假、病假、事假、产假、陪产假、婚假、丧假、调休等

### 审批流程
1. **员工提交请假申请**
2. **部门经理审批**
3. **条件判断**：
   - 请假天数 >= 5天 或 特殊假期类型（产假、陪产假） → 需要HR审批
   - 其他情况 → 直接完成审批
4. **HR审批**（如果需要）
5. **审批结果处理**

## 技术架构

### 核心技术
- **Spring Boot 3.5.3**：核心框架
- **Activiti 7.16.0**：工作流引擎
- **MyBatis Plus 3.5.12**：数据持久化
- **PostgreSQL**：数据库
- **Maven**：项目构建

### 项目结构
```
leave-service/
├── src/main/java/org/max/cms/leave/
│   ├── controller/          # REST控制器
│   ├── service/            # 业务逻辑服务
│   ├── entity/             # 实体类
│   ├── dto/                # 数据传输对象
│   ├── mapper/             # 数据访问接口
│   ├── enums/              # 枚举类
│   └── config/             # 配置类
└── src/main/resources/
    └── processes/          # Activiti工作流定义
```

## 数据模型

### 请假申请表 (leave_applications)
| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGSERIAL | 主键ID |
| applicant_id | BIGINT | 申请人ID |
| applicant_name | VARCHAR(100) | 申请人姓名 |
| department_id | BIGINT | 部门ID |
| department_name | VARCHAR(100) | 部门名称 |
| leave_type | VARCHAR(50) | 请假类型 |
| start_date | DATE | 开始日期 |
| end_date | DATE | 结束日期 |
| days | DECIMAL(5,2) | 请假天数 |
| reason | TEXT | 请假原因 |
| status | VARCHAR(20) | 请假状态 |
| process_instance_id | VARCHAR(100) | 工作流实例ID |
| approval_remark | TEXT | 审批备注 |

### 审批历史表 (leave_approval_history)
| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGSERIAL | 主键ID |
| leave_application_id | BIGINT | 请假申请ID |
| approver_id | BIGINT | 审批人ID |
| approver_name | VARCHAR(100) | 审批人姓名 |
| action | VARCHAR(20) | 审批动作 |
| comment | TEXT | 审批意见 |
| approval_time | TIMESTAMP | 审批时间 |

## API接口

### 请假申请管理

#### 创建请假申请
```http
POST /api/leave/applications
Content-Type: application/json

{
  "applicantId": 1,
  "leaveType": "ANNUAL",
  "startDate": "2024-01-15",
  "endDate": "2024-01-17",
  "days": 3,
  "reason": "个人休假"
}
```

#### 查询请假申请
```http
GET /api/leave/applications?page=1&size=10&applicantId=1&status=PENDING
```

#### 获取请假申请详情
```http
GET /api/leave/applications/{id}
```

#### 更新请假申请
```http
PUT /api/leave/applications/{id}
Content-Type: application/json

{
  "leaveType": "SICK",
  "startDate": "2024-01-15",
  "endDate": "2024-01-16",
  "days": 1,
  "reason": "感冒请假"
}
```

#### 删除请假申请
```http
DELETE /api/leave/applications/{id}
```

#### 取消请假申请
```http
POST /api/leave/applications/{id}/cancel?userId=1
```

### 审批管理

#### 获取待审批申请
```http
GET /api/leave/pending-approvals?page=1&size=10&approverId=2
```

#### 审批请假申请
```http
POST /api/leave/approvals
Content-Type: application/json

{
  "leaveApplicationId": 1,
  "approverId": 2,
  "action": "approve",
  "comment": "同意请假",
  "taskId": "task-123"
}
```

#### 获取审批历史
```http
GET /api/leave/applications/{id}/history
```

## 枚举定义

### 请假类型 (LeaveType)
- `ANNUAL` - 年假
- `SICK` - 病假
- `PERSONAL` - 事假
- `MATERNITY` - 产假
- `PATERNITY` - 陪产假
- `MARRIAGE` - 婚假
- `BEREAVEMENT` - 丧假
- `COMPENSATORY` - 调休
- `OTHER` - 其他

### 请假状态 (LeaveStatus)
- `PENDING` - 待审核
- `APPROVED` - 已批准
- `REJECTED` - 已拒绝
- `CANCELLED` - 已取消
- `IN_PROGRESS` - 进行中
- `COMPLETED` - 已完成

## 工作流程配置

### 流程定义 (LeaveProcess.bpmn20.xml)
工作流程包含以下节点：
1. **开始事件**
2. **部门经理审批**
3. **条件判断**（是否需要HR审批）
4. **HR审批**（可选）
5. **审批结果处理**
6. **结束事件**

### 流程变量
- `applicantId` - 申请人ID
- `leaveApplicationId` - 请假申请ID
- `leaveType` - 请假类型
- `days` - 请假天数
- `departmentId` - 部门ID
- `approved` - 审批结果
- `comment` - 审批意见

## 使用指南

### 1. 员工申请请假
1. 员工登录系统
2. 选择请假类型和时间
3. 填写请假原因
4. 提交申请
5. 系统自动启动工作流

### 2. 部门经理审批
1. 部门经理登录系统
2. 查看待审批申请
3. 查看申请详情
4. 审批通过或拒绝
5. 填写审批意见

### 3. HR审批（如果需要）
1. HR登录系统
2. 查看待审批申请
3. 最终审批决定
4. 系统更新申请状态

### 4. 查看审批历史
1. 任何时候都可以查看申请的审批历史
2. 包含所有审批人的操作记录
3. 审批时间和意见完整记录

## 部署和配置

### 1. 数据库配置
确保PostgreSQL数据库已创建相关表：
```sql
-- 执行数据库迁移脚本
-- V000006__add_leave_management.sql
```

### 2. 应用配置
在 `application.yml` 中配置：
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/minicms
    username: dbuser
    password: 123qwe
    
activiti:
  database-schema-update: true
  db-identity-used: false
  history-level: full
```

### 3. 启动应用
```bash
cd cms
mvn spring-boot:run -pl bootloader
```

### 4. 访问API文档
启动后访问：http://localhost:8080/api/swagger-ui.html

## 注意事项

### 权限控制
- 员工只能创建、修改和取消自己的请假申请
- 部门经理可以审批本部门员工的请假申请
- HR可以审批需要HR审批的请假申请
- 系统管理员可以查看所有请假申请

### 工作流程规则
1. **短期请假**（< 5天）：只需部门经理审批
2. **长期请假**（>= 5天）：需要部门经理和HR双重审批
3. **特殊假期**（产假、陪产假）：需要HR审批
4. **状态变更**：只有待审核状态的申请才能修改或删除

### 数据完整性
- 所有外键约束确保数据一致性
- 审批历史完整记录不可修改
- 工作流实例与申请记录关联
- 软删除保留历史数据

## 扩展功能

### 可扩展的功能点
1. **邮件通知**：审批结果邮件通知
2. **移动端支持**：手机端请假申请
3. **报表统计**：请假统计和分析
4. **假期余额**：员工假期余额管理
5. **批量操作**：批量审批功能
6. **自定义流程**：可配置的审批流程

### 集成建议
1. **OA系统集成**：与现有OA系统集成
2. **考勤系统集成**：与考勤系统数据同步
3. **薪资系统集成**：假期影响薪资计算
4. **消息推送**：实时消息推送功能

## 故障排除

### 常见问题

#### 1. 工作流启动失败
**解决方案**：
- 检查流程定义文件是否正确
- 确认数据库连接正常
- 检查Activiti相关表是否创建

#### 2. 审批任务无法完成
**解决方案**：
- 检查任务分配是否正确
- 确认审批人权限
- 查看工作流程日志

#### 3. 数据库连接错误
**解决方案**：
- 检查数据库配置
- 确认数据库服务运行
- 检查网络连接

## 技术支持

如有问题请联系：
- 技术支持：tech-support@company.com
- 项目维护：project-team@company.com

---

*本文档最后更新时间：2024年12月*