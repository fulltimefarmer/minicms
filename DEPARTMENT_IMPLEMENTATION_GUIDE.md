# CMS 部门管理功能实现指南

## 概述

本文档记录了为CMS（Company Management System）添加员工部门层级树功能的完整实现过程。该功能包括：

1. 新增部门表和相关后端服务
2. 修改用户表以支持部门关联
3. 实现前端部门管理页面
4. 更新用户管理页面以支持部门选择

## 后端实现

### 1. 数据库结构

#### 新增部门表
```sql
-- 部门表
CREATE TABLE departments (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(500),
    parent_id BIGINT,
    path VARCHAR(500),
    level INTEGER DEFAULT 1,
    sort INTEGER DEFAULT 0,
    manager VARCHAR(100),
    phone VARCHAR(20),
    email VARCHAR(100),
    address VARCHAR(200),
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255) DEFAULT 'system',
    updated_by VARCHAR(255) DEFAULT 'system',
    deleted BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (parent_id) REFERENCES departments(id)
);
```

#### 修改用户表
```sql
-- 为用户表添加部门关联
ALTER TABLE users ADD COLUMN department_id BIGINT;
ALTER TABLE users ADD CONSTRAINT fk_users_department 
    FOREIGN KEY (department_id) REFERENCES departments(id);
```

### 2. 实体类

#### 部门实体 (Department.java)
- 支持层级结构
- 包含完整的部门信息字段
- 支持树形结构的children字段

#### 用户实体 (User.java)
- 添加departmentId字段关联部门
- 添加departmentName字段用于显示

### 3. 服务层

#### 部门服务 (DepartmentService)
- 支持CRUD操作
- 提供树形结构构建
- 支持层级管理和路径计算

#### 用户服务 (UserService)
- 扩展用户管理功能
- 支持按部门查询用户

### 4. 控制器

#### 部门控制器 (DepartmentController)
- 提供RESTful API
- 支持树形结构查询
- 支持部门的增删改查

#### 用户控制器 (UserController)
- 扩展用户管理API
- 支持按部门查询用户

## 前端实现

### 1. 部门管理页面

#### 功能特性
- 树形结构展示部门架构
- 支持展开/收起节点
- 支持添加、编辑、删除部门
- 支持层级关系管理

#### 关键组件
- `DepartmentManagementComponent`: 主要组件
- 树形结构渲染
- 模态框表单
- 部门选择器

### 2. 用户管理页面更新

#### 新增功能
- 部门筛选器
- 用户表格显示部门信息
- 用户表单中的部门选择

#### 更新内容
- 添加部门相关接口
- 更新用户数据结构
- 添加部门过滤逻辑

### 3. 路由配置

```typescript
const routes: Routes = [
  { path: 'departments', component: DepartmentManagementComponent, canActivate: [AuthGuard] },
  // 其他路由...
];
```

### 4. 导航更新

在仪表板中添加部门管理入口。

## 数据库迁移

### 迁移脚本
文件：`cms/scripts/db/migration/V2__Add_departments_table.sql`

包含：
- 部门表创建
- 用户表字段添加
- 索引创建
- 示例数据插入

### 示例数据
插入了完整的组织架构示例：
- 总经理办公室
- 技术部（前端组、后端组、测试组）
- 市场部（产品组、销售组）
- 人事部
- 财务部

## API接口

### 部门管理API
- `GET /api/departments/tree` - 获取部门树形结构
- `GET /api/departments` - 获取部门列表
- `POST /api/departments` - 创建部门
- `PUT /api/departments/{id}` - 更新部门
- `DELETE /api/departments/{id}` - 删除部门

### 用户管理API
- `GET /api/users` - 获取用户列表
- `GET /api/users/department/{departmentId}` - 按部门获取用户

## 部署说明

### 1. 数据库迁移
```bash
# 运行数据库迁移脚本
psql -h localhost -U username -d database_name -f V2__Add_departments_table.sql
```

### 2. 后端部署
```bash
# 构建用户服务
cd cms/user-service
mvn clean package

# 启动服务
java -jar target/user-service.jar
```

### 3. 前端部署
```bash
# 安装依赖
cd frontend
npm install

# 构建生产版本
npm run build

# 启动开发服务器
npm run start
```

## 功能验证

### 1. 后端验证
- 部门API接口测试
- 用户API接口测试
- 数据库数据完整性验证

### 2. 前端验证
- 部门管理页面功能测试
- 用户管理页面部门功能测试
- 树形结构展示验证

## 技术要点

### 1. 树形结构处理
- 使用递归算法构建树形结构
- 支持无限层级嵌套
- 优化查询性能

### 2. 数据一致性
- 外键约束保证数据完整性
- 软删除避免数据丢失
- 事务处理确保操作原子性

### 3. 前端优化
- 组件化设计
- 响应式布局
- 用户体验优化

## 扩展建议

1. **权限控制**: 基于部门的权限管理
2. **数据统计**: 部门维度的数据统计
3. **批量操作**: 用户的批量部门转移
4. **导入导出**: 部门架构的导入导出功能
5. **历史记录**: 部门变更历史追踪

## 总结

本次实现成功为CMS系统添加了完整的部门层级管理功能，包括：

- ✅ 数据库结构设计和迁移
- ✅ 后端服务和API实现
- ✅ 前端管理界面开发
- ✅ 用户-部门关联功能
- ✅ 树形结构展示和管理

该功能为企业管理系统提供了基础的组织架构管理能力，为后续的权限管理、数据统计等功能奠定了基础。