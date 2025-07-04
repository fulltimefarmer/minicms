# 用户管理系统

这是一个基于 Angular 的用户管理系统，提供完整的用户 CRUD（创建、读取、更新、删除）功能。

## 功能特性

### 🎯 核心功能
- **用户列表展示** - 以表格形式展示所有用户信息
- **用户搜索** - 支持按姓名和邮箱搜索用户
- **角色筛选** - 按用户角色（管理员、普通用户、访客）筛选
- **状态筛选** - 按用户状态（激活、停用）筛选
- **添加用户** - 通过模态框表单添加新用户
- **编辑用户** - 修改用户信息
- **状态切换** - 快速激活/停用用户
- **删除用户** - 删除用户（带确认提示）
- **统计面板** - 显示总用户数、激活用户数、停用用户数

### 🎨 界面特性
- **响应式设计** - 支持桌面和移动设备
- **现代化 UI** - 美观的用户界面设计
- **表单验证** - 完整的客户端表单验证
- **状态指示** - 清晰的角色和状态标识
- **操作确认** - 危险操作需要用户确认

## 快速开始

### 安装依赖
```bash
cd frontend
npm install
```

### 启动开发服务器
```bash
npm start
```

应用将在 `http://localhost:4200` 启动，默认页面就是用户管理界面。

## 用户操作指南

### 查看用户列表
- 打开应用后直接显示用户管理页面
- 用户信息以表格形式展示，包含 ID、姓名、邮箱、电话、角色、状态、创建时间

### 搜索用户
- 在顶部搜索框中输入用户姓名或邮箱
- 搜索结果实时更新

### 筛选用户
- 使用角色下拉菜单筛选特定角色的用户
- 使用状态下拉菜单筛选激活或停用的用户
- 可组合多个筛选条件

### 添加新用户
1. 点击页面右上角的"+ 添加用户"按钮
2. 在弹出的模态框中填写用户信息：
   - 姓名（必填）
   - 邮箱（必填，需要有效邮箱格式）
   - 电话（可选）
   - 角色（必选：管理员、普通用户、访客）
   - 状态（默认激活）
3. 点击"保存"按钮完成添加

### 编辑用户
1. 在用户列表中找到要编辑的用户
2. 点击该用户行的"编辑"按钮
3. 在弹出的模态框中修改用户信息
4. 点击"更新"按钮保存修改

### 状态管理
- 点击用户行的"停用"按钮将激活用户设为停用状态
- 点击用户行的"激活"按钮将停用用户设为激活状态
- 停用的用户在表格中会显示为半透明状态

### 删除用户
1. 点击用户行的"删除"按钮
2. 在确认对话框中点击"确定"
3. 用户将从系统中永久删除

## 技术特性

### 前端框架
- **Angular 20** - 最新版本的 Angular 框架
- **Standalone Components** - 使用独立组件架构
- **TypeScript** - 类型安全的 JavaScript 超集

### UI 组件
- **响应式布局** - 使用 CSS Grid 和 Flexbox
- **表单处理** - Angular Forms 模块
- **路由系统** - Angular Router

### 数据管理
- **本地数据** - 演示数据存储在组件中
- **类型定义** - 完整的 TypeScript 接口定义
- **状态管理** - 组件级状态管理

## 样例数据

系统预置了以下样例用户：

| 姓名 | 邮箱 | 角色 | 状态 |
|------|------|------|------|
| 张三 | zhangsan@example.com | 管理员 | 激活 |
| 李四 | lisi@example.com | 普通用户 | 激活 |
| 王五 | wangwu@example.com | 普通用户 | 停用 |
| 赵六 | zhaoliu@example.com | 访客 | 激活 |

## 路由配置

- `/` - 默认重定向到用户管理页面
- `/users` - 用户管理页面
- `/login` - 登录页面（保留，但不是默认页面）

## 部署说明

### 构建生产版本
```bash
npm run build
```

### 预览生产版本
```bash
npm run serve:ssr:frontend-app
```

## 自定义扩展

### 添加新字段
1. 在 `User` 接口中添加新字段
2. 更新用户管理组件的模板
3. 更新表单验证逻辑

### 集成后端 API
1. 创建 Angular 服务
2. 替换组件中的本地数据操作
3. 添加 HTTP 客户端调用

### 添加权限控制
1. 实现认证服务
2. 添加路由守卫
3. 根据用户权限显示/隐藏操作按钮

## 浏览器兼容性

支持所有现代浏览器：
- Chrome 90+
- Firefox 88+
- Safari 14+
- Edge 90+

---

## 技术支持

如果您在使用过程中遇到任何问题，请检查：
1. Node.js 版本是否为 18+ 
2. Angular CLI 是否已正确安装
3. 所有依赖是否已正确安装

该用户管理系统为演示项目，生产环境使用请根据实际需求进行安全性和性能优化。