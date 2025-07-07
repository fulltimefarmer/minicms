# 员工信息管理功能实现说明

## 概述

在user-service中添加了完整的员工信息管理功能，包含员工信息、薪资管理、职级管理、劳动合同管理和联系方式管理等模块。

## 功能模块

### 1. 员工信息管理 (Employee Management)

#### 实体类: `Employee.java`
- 员工基本信息维护
- 关联用户信息
- 包含工号、身份证、入职日期等基础信息
- 支持员工状态管理 (ACTIVE/INACTIVE/TERMINATED)

#### 主要功能:
- 员工信息的增删改查
- 根据用户ID、工号、部门ID查询员工
- 批量导入员工信息
- 获取员工完整信息（含职级、薪资等）

### 2. 职级管理 (Job Level Management)

#### 实体类: `JobLevel.java`
- 职级体系管理
- 包含职级名称、代码、等级、薪资范围等
- 支持职级启用/禁用

#### 主要功能:
- 职级的增删改查
- 职级代码管理
- 等级范围查询
- 职级状态管理

### 3. 薪资管理 (Salary Management)

#### 实体类: `Salary.java`
- 详细的薪资结构
- 包含基本工资、各种津贴、扣款项目
- 自动计算应发工资和实发工资
- 支持薪资历史记录

#### 主要功能:
- 薪资信息管理
- 薪资调整和批量调整
- 薪资历史查询
- 工资单生成
- 净工资计算

### 4. 劳动合同管理 (Contract Management)

#### 实体类: `Contract.java`
- 劳动合同信息管理
- 支持一对多关系（一个员工可有多个合同）
- 包含合同类型、期限、薪资等信息
- 支持合同状态管理

#### 主要功能:
- 合同的增删改查
- 合同续签和终止
- 即将到期合同提醒
- 合同编号自动生成
- 批量合同操作

### 5. 联系方式管理 (Contact Management)

#### 实体类: `Contact.java`
- 多种联系方式支持
- 包含地址、电话、邮箱、紧急联系人等
- 支持主要联系方式设置

#### 主要功能:
- 联系方式维护
- 主要联系方式设置
- 按类型查询联系方式
- 批量联系方式管理

## 数据库设计

### 表结构
1. **employees** - 员工信息表
2. **job_levels** - 职级表
3. **salaries** - 薪资表
4. **contracts** - 劳动合同表
5. **contacts** - 联系方式表

### 关系设计
- Employee -> User (多对一)
- Employee -> Salary (一对多)
- Employee -> Contract (一对多)
- Employee -> Contact (一对多)
- Salary -> JobLevel (多对一)

## API接口

### 员工管理接口 (`/api/employees`)
- `GET /` - 获取所有员工
- `GET /{id}` - 获取员工详情
- `GET /{id}/details` - 获取员工完整信息
- `GET /user/{userId}` - 根据用户ID获取员工
- `GET /number/{employeeNumber}` - 根据工号获取员工
- `POST /` - 创建员工
- `PUT /{id}` - 更新员工信息
- `DELETE /{id}` - 删除员工
- `POST /batch` - 批量导入员工

### 职级管理接口 (`/api/job-levels`)
- `GET /` - 获取所有职级
- `GET /enabled` - 获取启用的职级
- `GET /{id}` - 获取职级详情
- `GET /code/{code}` - 根据代码获取职级
- `POST /` - 创建职级
- `PUT /{id}` - 更新职级
- `DELETE /{id}` - 删除职级
- `PUT /{id}/toggle` - 启用/禁用职级

### 薪资管理接口 (`/api/salaries`)
- `GET /{id}` - 获取薪资详情
- `GET /employee/{employeeId}/current` - 获取员工当前薪资
- `GET /employee/{employeeId}/history` - 获取薪资历史
- `POST /` - 创建薪资记录
- `PUT /{id}` - 更新薪资
- `DELETE /{id}` - 删除薪资记录
- `POST /adjust` - 薪资调整
- `POST /batch-adjust` - 批量薪资调整
- `POST /calculate` - 计算净工资
- `POST /payslip` - 生成工资单

### 合同管理接口 (`/api/contracts`)
- `GET /{id}` - 获取合同详情
- `GET /employee/{employeeId}` - 获取员工所有合同
- `GET /employee/{employeeId}/current` - 获取员工当前合同
- `GET /number/{contractNumber}` - 根据合同编号获取
- `POST /` - 创建合同
- `PUT /{id}` - 更新合同
- `DELETE /{id}` - 删除合同
- `POST /{id}/renew` - 合同续签
- `POST /{id}/terminate` - 合同终止
- `GET /expiring` - 获取即将到期合同
- `POST /batch-renew` - 批量续签

### 联系方式接口 (`/api/contacts`)
- `GET /{id}` - 获取联系方式详情
- `GET /employee/{employeeId}` - 获取员工联系方式
- `GET /employee/{employeeId}/primary` - 获取主要联系方式
- `POST /` - 创建联系方式
- `PUT /{id}` - 更新联系方式
- `DELETE /{id}` - 删除联系方式
- `PUT /employee/{employeeId}/primary/{contactId}` - 设置主要联系方式
- `POST /batch` - 批量创建联系方式

## 业务特性

### 1. 薪资计算
- 自动计算应发工资和实发工资
- 支持各种津贴和扣款项目
- 薪资调整时自动创建历史记录

### 2. 合同管理
- 合同编号自动生成规则：CT + 年份 + 员工ID + 流水号
- 合同到期提醒功能
- 支持合同续签和终止操作

### 3. 数据完整性
- 外键约束确保数据一致性
- 软删除机制保护历史数据
- 索引优化查询性能

### 4. 扩展性设计
- 模块化设计，便于功能扩展
- 统一的BaseEntity基类
- 标准的增删改查操作

## 初始化数据

系统提供了8个标准职级的初始化数据：
1. 初级员工 (JL001)
2. 中级员工 (JL002)
3. 高级员工 (JL003)
4. 主管 (JL004)
5. 经理 (JL005)
6. 总监 (JL006)
7. 副总裁 (JL007)
8. 总裁 (JL008)

## 使用说明

### 1. 初始化数据库
```sql
-- 执行 cms/user-service/src/main/resources/sql/employee_management.sql
```

### 2. 创建员工流程
1. 首先创建用户 (User)
2. 创建员工基本信息 (Employee)
3. 添加联系方式 (Contact)
4. 创建薪资记录 (Salary)
5. 签署劳动合同 (Contract)

### 3. 薪资调整流程
1. 调用薪资调整接口
2. 系统自动结束当前薪资记录
3. 创建新的薪资记录
4. 自动计算净工资

### 4. 合同管理流程
1. 创建新合同
2. 合同到期前系统提醒
3. 执行续签或终止操作

## 注意事项

1. 员工信息与用户信息通过user_id关联
2. 薪资调整会自动创建历史记录
3. 合同编号自动生成，确保唯一性
4. 软删除机制保护重要数据
5. 所有货币金额使用DECIMAL(12,2)类型
6. 日期字段支持空值，表示无限期或未设置

## 技术栈

- **框架**: Spring Boot + MyBatis Plus
- **数据库**: MySQL 8.0+
- **注解**: Lombok + Spring Annotations
- **API**: RESTful API
- **数据库设计**: 关系型数据库设计

## 扩展建议

1. 添加员工考勤管理
2. 集成绩效考核系统
3. 添加员工培训记录
4. 集成组织架构管理
5. 添加员工档案管理
6. 实现薪资报表功能