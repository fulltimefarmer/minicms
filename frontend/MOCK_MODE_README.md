# CMS 前端 Mock 模式说明

## 概述

Mock 模式为 CMS 前端项目提供了完整的本地数据模拟，无需依赖后端服务即可运行和测试前端功能。

## 功能特性

- ✅ **完全离线运行** - 无需后端服务
- ✅ **真实API模拟** - 使用 json-server 提供完整的 REST API
- ✅ **数据持久化** - 修改的数据会保存到本地
- ✅ **认证模拟** - 包含登录、登出、token刷新等认证功能
- ✅ **全接口覆盖** - 涵盖所有 CMS 功能模块的 API

## 快速启动

### 方式一：使用启动脚本（推荐）

```bash
cd frontend
./start-mock.sh
```

### 方式二：手动启动

```bash
cd frontend

# 1. 安装依赖
npm install

# 2. 生成 Mock 数据
npm run mock:generate

# 3. 启动 Mock 模式
npm run start:mock
```

## 访问信息

- **前端地址**: http://localhost:4200
- **Mock API地址**: http://localhost:3001
- **默认登录账号**:
  - 用户名: `admin`
  - 密码: `123456`

## Mock 数据说明

### 涵盖的模块

1. **认证模块** (`/api/auth/*`)
   - 登录: `POST /api/auth/login`
   - 登出: `POST /api/auth/logout`
   - 刷新Token: `POST /api/auth/refresh`

2. **待办管理** (`/api/todos/*`)
   - 基础CRUD操作
   - 搜索、筛选、分页
   - 批量操作
   - 统计数据

3. **用户管理** (`/api/users/*`)
   - 用户信息管理
   - 角色分配

4. **权限管理** (`/api/roles/*`, `/api/permissions/*`)
   - 角色管理
   - 权限配置

5. **字典管理** (`/api/dict-types/*`, `/api/dict-items/*`)
   - 字典类型管理
   - 字典项管理

6. **部门管理** (`/api/departments/*`)
   - 部门树形结构
   - 部门层级关系

7. **资产管理** (`/api/assets/*`)
   - 资产信息管理
   - 资产分配和回收

### 数据量统计

- **待办事项**: 25条
- **用户**: 8个
- **角色**: 3个
- **权限**: 10个
- **字典类型**: 3个
- **字典项**: 8个
- **部门**: 6个（含层级）
- **资产**: 20个

## 命令说明

### 核心命令

```bash
# 启动 Mock 模式（包含前端和 Mock API）
npm run start:mock

# 单独启动 Mock API 服务器
npm run mock:server

# 重新生成 Mock 数据
npm run mock:generate

# 普通开发模式（连接真实后端）
npm start
```

### Mock 服务器配置

- **端口**: 3001
- **数据文件**: `src/mock/db.json`
- **路由配置**: `src/mock/routes.json`
- **中间件**: `src/mock/middleware.js`

## 文件结构

```
frontend/
├── src/
│   ├── environments/
│   │   ├── environment.ts           # 默认环境配置
│   │   └── environment.mock.ts      # Mock 模式环境配置
│   └── mock/
│       ├── db.json                  # Mock 数据库文件
│       ├── routes.json              # API 路由映射
│       ├── middleware.js            # 请求中间件
│       └── generate-data.js         # 数据生成脚本
├── proxy.conf.json                  # 代理配置
├── start-mock.sh                    # Mock 模式启动脚本
└── package.json                     # 包含 Mock 相关命令
```

## 环境切换

项目通过 Angular 环境配置实现不同模式的切换：

### 开发模式 (默认)
```typescript
// environment.ts
export const environment = {
  production: false,
  mock: false,
  apiUrl: 'http://localhost:8080/api'
};
```

### Mock 模式
```typescript
// environment.mock.ts
export const environment = {
  production: false,
  mock: true,
  apiUrl: 'http://localhost:3001/api'
};
```

## 自定义 Mock 数据

### 修改现有数据

1. 编辑 `src/mock/generate-data.js`
2. 修改相应的数据生成函数
3. 重新运行 `npm run mock:generate`

### 添加新接口

1. 在 `src/mock/routes.json` 中添加路由映射
2. 在 `src/mock/middleware.js` 中添加特殊逻辑处理
3. 在 `src/mock/generate-data.js` 中添加对应数据

### 示例：添加新的API接口

```javascript
// routes.json
{
  "/api/new-module": "/newModule"
}

// generate-data.js
function generateNewModule() {
  return [
    { id: 1, name: "示例数据" }
  ];
}

// 在 generateDatabase() 中添加
const data = {
  // ... 其他数据
  newModule: generateNewModule()
};
```

## 故障排除

### 常见问题

1. **端口冲突**
   - 前端端口 4200 被占用：修改 `angular.json` 中的端口配置
   - Mock API 端口 3001 被占用：修改 `package.json` 中 `mock:server` 命令的端口

2. **依赖安装失败**
   ```bash
   rm -rf node_modules package-lock.json
   npm install
   ```

3. **Mock 数据没有更新**
   ```bash
   npm run mock:generate
   ```

4. **API 请求失败**
   - 检查代理配置 `proxy.conf.json`
   - 确认环境变量配置正确

### 调试技巧

1. **查看 Mock API 请求**
   - 访问 http://localhost:3001 查看所有可用接口
   - 在浏览器开发者工具中查看网络请求

2. **修改日志级别**
   - 在 `proxy.conf.json` 中设置 `"logLevel": "debug"`

## 技术架构

### 核心技术栈

- **json-server**: 提供 RESTful API 模拟
- **concurrently**: 并发运行多个命令
- **Angular 环境配置**: 实现环境切换
- **HTTP 代理**: 请求转发

### 工作原理

1. **环境检测**: Angular 根据 `--configuration=mock` 加载 mock 环境配置
2. **请求代理**: 通过 `proxy.conf.json` 将 `/api/*` 请求转发到 Mock 服务器
3. **数据模拟**: json-server 根据 `db.json` 提供 REST API
4. **请求拦截**: middleware.js 处理认证、格式化等特殊逻辑

## 生产部署注意事项

⚠️ **重要**: Mock 模式仅用于开发和测试，不应在生产环境中使用。

在部署到生产环境时：
1. 使用 `npm run build` 而不是 mock 相关命令
2. 确保环境配置指向真实的后端 API
3. 移除或忽略 `src/mock/` 目录

---

如有问题，请查看项目文档或联系开发团队。