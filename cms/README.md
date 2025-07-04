# Company Management System (CMS)

基于Spring Boot的多模块企业管理系统，采用RBAC权限模型，支持用户管理、权限管理、资产管理等功能。

## 项目架构

这是一个多模块项目（非微服务），包含以下模块：

### 模块结构

```
company-management-system/
├── common-service/         # 公共模块
│   ├── 审计日志功能
│   ├── 基础实体类
│   ├── 通用工具类
│   └── 公共配置
├── auth-service/          # 权限模块
│   ├── RBAC权限管理
│   ├── 角色管理
│   └── 权限管理
├── user-service/          # 用户管理模块
│   ├── 用户管理
│   ├── 用户认证
│   └── 登录功能
├── asset-service/         # 资产管理模块
│   ├── 资产管理
│   ├── 资产分类
│   └── 资产状态管理
└── bootloader/           # 启动模块
    ├── 应用启动器
    ├── 配置集成
    └── 数据库迁移
```

## 技术栈

- **框架**: Spring Boot 3.5.3
- **JDK**: Java 21
- **数据库**: PostgreSQL
- **ORM**: MyBatis Plus
- **数据库迁移**: Flyway
- **安全**: Spring Security + JWT
- **文档**: Swagger/OpenAPI 3
- **构建工具**: Maven
- **其他组件**:
  - Lombok (代码简化)
  - Apache Commons Lang3 (工具类)
  - Spring DevTools (开发工具)

## 快速开始

### 1. 环境要求

- JDK 21+
- Maven 3.8+
- PostgreSQL 12+

### 2. 数据库准备

```sql
-- 创建数据库
CREATE DATABASE cms;

-- 创建用户
CREATE USER cms_user WITH PASSWORD 'cms_password';

-- 授权
GRANT ALL PRIVILEGES ON DATABASE cms TO cms_user;
```

### 3. 配置修改

修改 `bootloader/src/main/resources/application.yml` 中的数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/cms
    username: cms_user
    password: cms_password
```

### 4. 运行应用

```bash
# 进入项目目录
cd cms

# 编译项目
mvn clean compile

# 运行应用
mvn spring-boot:run -pl bootloader
```

### 5. 访问应用

- 应用地址: http://localhost:8080/api
- Swagger文档: http://localhost:8080/api/swagger-ui.html
- 健康检查: http://localhost:8080/api/actuator/health

## 默认账户

系统预置了以下测试账户：

| 用户名 | 密码 | 角色 | 说明 |
|--------|------|------|------|
| admin | admin123 | SUPER_ADMIN | 超级管理员 |
| manager | admin123 | ADMIN | 管理员 |
| user | admin123 | USER | 普通用户 |

## 功能特性

### 🔐 权限管理
- 基于RBAC模型的权限控制
- 支持角色继承和权限分配
- 细粒度的API和菜单权限控制

### 👥 用户管理
- 用户CRUD操作
- 用户状态管理
- 密码重置和修改
- 登录失败锁定机制

### 📊 审计日志
- 自动记录用户操作
- 请求响应日志记录
- 操作时间和IP地址跟踪
- 支持日志查询和过滤

### 🏢 资产管理
- 公司资产登记和管理
- 资产状态跟踪
- 资产分类和搜索
- 资产责任人管理

### 📚 API文档
- 集成Swagger UI
- 自动生成API文档
- 支持在线测试

## 开发指南

### 模块开发规范

1. **实体类**: 继承 `BaseEntity`，包含审计字段
2. **Repository**: 使用 MyBatis Plus 的 `BaseMapper`
3. **Service**: 标准的服务层模式
4. **Controller**: RESTful API设计
5. **审计日志**: 使用 `@Auditable` 注解自动记录

### 添加新功能

1. 在相应模块中创建实体类
2. 创建Repository接口
3. 实现Service层逻辑
4. 创建Controller提供API
5. 编写Flyway迁移脚本
6. 添加权限配置

### 数据库迁移

使用Flyway管理数据库版本：

```bash
# 查看迁移状态
mvn flyway:info -pl bootloader

# 执行迁移
mvn flyway:migrate -pl bootloader

# 清理数据库（慎用）
mvn flyway:clean -pl bootloader
```

## 部署

### Docker部署

```bash
# 构建应用
mvn clean package -pl bootloader

# 运行PostgreSQL
docker run -d \
  --name postgres \
  -e POSTGRES_DB=cms \
  -e POSTGRES_USER=cms_user \
  -e POSTGRES_PASSWORD=cms_password \
  -p 5432:5432 \
  postgres:13

# 运行应用
java -jar bootloader/target/bootloader-0.0.1-SNAPSHOT.jar
```

## 贡献指南

1. Fork项目
2. 创建功能分支
3. 提交代码
4. 发起Pull Request

## 许可证

本项目采用MIT许可证。