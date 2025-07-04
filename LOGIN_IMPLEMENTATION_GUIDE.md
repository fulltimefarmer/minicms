# 用户登录系统实现指南

本文档描述了在CMS后端、前端（Angular）和移动端（React Native）中实现的完整用户登录系统。

## 系统架构

### 1. 后端 (CMS - Spring Boot)
- **技术栈**: Spring Boot 3.5.3, Java 21, PostgreSQL, MyBatis Plus, JWT
- **认证方式**: JWT Token
- **安全框架**: Spring Security

### 2. 前端 (Angular 20)
- **技术栈**: Angular 20, TypeScript, RxJS
- **认证管理**: AuthService + AuthGuard
- **路由保护**: 路由守护自动跳转

### 3. 移动端 (React Native)
- **技术栈**: React Native 0.74, TypeScript, React Navigation
- **状态管理**: React Context API
- **本地存储**: AsyncStorage

## 功能特性

### ✅ 核心功能
- [x] 用户登录认证
- [x] JWT Token 生成和验证
- [x] Token 自动刷新
- [x] 登录状态管理
- [x] 路由守护（未登录自动跳转到登录页）
- [x] 安全的密码加密（BCrypt）
- [x] 跨平台支持（Web + Mobile）

### ✅ 安全特性
- [x] CORS 配置
- [x] Token 过期自动处理
- [x] 安全的本地存储
- [x] 密码强加密
- [x] API 请求拦截器

## 部署和运行

### 1. 后端启动

```bash
cd cms
# 启动数据库
docker-compose up -d

# 运行数据库迁移
./mvnw flyway:migrate

# 启动应用
./mvnw spring-boot:run -pl auth-service
```

### 2. 前端启动

```bash
cd frontend
# 安装依赖
npm install

# 启动开发服务器
npm start
```

### 3. 移动端启动

```bash
cd mobile
# 安装依赖
npm install

# iOS
npm run ios

# Android
npm run android
```

## 测试账号

数据库初始化时会创建以下测试账号：

| 用户名 | 密码 | 角色 | 描述 |
|--------|------|------|------|
| admin | 123456 | 超级管理员 | 拥有所有权限 |
| editor | 123456 | 编辑者 | 部分权限 |

## API 接口

### 认证相关接口

#### 1. 用户登录
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "123456"
}
```

**响应示例:**
```json
{
  "success": true,
  "message": "操作成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "userId": 1,
    "username": "admin",
    "email": "admin@example.com",
    "nickname": "系统管理员",
    "avatar": null,
    "roles": ["ROLE_SUPER_ADMIN"],
    "permissions": ["user:manage", "role:manage"]
  }
}
```

#### 2. 验证 Token
```http
GET /api/auth/validate
Authorization: Bearer {token}
```

#### 3. 刷新 Token
```http
POST /api/auth/refresh
Authorization: Bearer {token}
```

#### 4. 用户登出
```http
POST /api/auth/logout
Authorization: Bearer {token}
```

## 配置说明

### 1. 后端配置

**JWT 配置** (`application.yml`):
```yaml
jwt:
  secret: mySecretKey  # 建议使用更安全的密钥
  expiration: 86400000  # 24小时 (毫秒)
```

**数据库配置**:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/minicms
    username: dbuser
    password: 123qwe
```

### 2. 前端配置

**API 基础地址** (`src/app/auth.service.ts`):
```typescript
private apiUrl = 'http://localhost:8080/api/auth';
```

### 3. 移动端配置

**API 基础地址** (`src/services/AuthService.ts`):
```typescript
private apiUrl = 'http://localhost:8080/api/auth';
```

## 用户流程

### 登录流程
1. 用户访问任意受保护的页面
2. 系统检查是否已登录（本地是否有有效 token）
3. 如未登录，自动跳转到登录页面
4. 用户输入用户名和密码
5. 前端调用登录 API
6. 后端验证用户信息，生成 JWT token
7. 前端收到 token，保存到本地存储
8. 跳转到用户访问的原始页面（或默认首页）

### 认证检查流程
1. 每次访问受保护的路由时，AuthGuard 检查登录状态
2. 如果 token 存在，验证 token 有效性
3. 如果 token 有效，允许访问
4. 如果 token 无效或过期，自动跳转到登录页

### 自动刷新流程
1. API 请求时，拦截器自动添加 Authorization 头
2. 如果收到 401 响应，尝试刷新 token
3. 刷新成功后，重新发送原始请求
4. 刷新失败则跳转到登录页

## 技术实现细节

### 1. 后端实现

**核心类:**
- `AuthController`: 认证接口控制器
- `AuthService`: 认证业务逻辑
- `JwtUtil`: JWT 工具类
- `SecurityConfig`: Spring Security 配置
- `JwtAuthenticationFilter`: JWT 认证过滤器

### 2. 前端实现

**核心组件:**
- `AuthService`: 认证服务
- `AuthGuard`: 路由守护
- `AuthInterceptor`: HTTP 拦截器
- `LoginComponent`: 登录组件
- `DashboardComponent`: 仪表板组件

### 3. 移动端实现

**核心组件:**
- `AuthService`: 认证服务
- `AuthContext`: 认证上下文
- `AuthProvider`: 认证提供者
- `LoginScreen`: 登录屏幕
- `DashboardScreen`: 仪表板屏幕

## 安全考虑

1. **密码安全**: 使用 BCrypt 加密存储
2. **Token 安全**: JWT 使用 HMAC-SHA256 签名
3. **HTTPS**: 生产环境建议使用 HTTPS
4. **CORS**: 配置适当的跨域策略
5. **Token 过期**: 设置合理的过期时间
6. **刷新机制**: 实现 token 自动刷新

## 扩展功能建议

1. **记住我功能**: 扩展 token 有效期
2. **多设备登录**: 支持同一用户多设备登录
3. **登录日志**: 记录登录历史
4. **双因子认证**: 增加短信或邮箱验证
5. **社交登录**: 支持第三方登录（微信、QQ等）
6. **密码策略**: 实现密码复杂度要求
7. **账户锁定**: 多次登录失败后锁定账户

## 故障排除

### 常见问题

1. **CORS 错误**: 检查后端 CORS 配置
2. **Token 无效**: 检查 JWT 密钥和过期时间
3. **数据库连接失败**: 检查数据库配置和连接
4. **路由跳转问题**: 检查 Angular 路由配置
5. **移动端网络问题**: 检查 API 地址和网络权限

### 调试技巧

1. **查看浏览器控制台**: 检查网络请求和错误日志
2. **检查本地存储**: 验证 token 是否正确保存
3. **后端日志**: 查看 Spring Boot 日志输出
4. **数据库查询**: 验证用户数据是否正确

## 更新日志

- **v1.0.0**: 初始版本，实现基础登录功能
- **功能完成**: 后端 JWT 认证、前端路由守护、移动端状态管理

---

## 开发团队

如有问题或建议，请联系开发团队。