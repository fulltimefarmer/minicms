# 用户密码明文保存修改总结

## 修改概述
根据要求，将user表中的password字段改为明文保存，所有用户的初始化密码设置为"1234"。

## 修改内容

### 1. 数据库修改
- **文件**: `cms/scripts/db/migration/V000002__update_passwords_to_plaintext.sql`
  - 新增迁移脚本，将所有现有用户的密码更新为明文"1234"
  - 添加数据库注释说明密码现在以明文形式存储

### 2. 初始化数据修改
- **文件**: `cms/scripts/db/migration/V000002__initial_baseline_data.sql`
  - 将默认用户的密码从BCrypt加密值改为明文"1234"
  - 更新了所有默认用户账号：admin、manager、editor、user

### 3. 认证服务修改
- **文件**: `cms/auth-service/src/main/java/org/max/cms/auth/service/impl/AuthServiceImpl.java`
  - 移除了对`PasswordEncoder`的依赖注入
  - 将密码验证逻辑从`passwordEncoder.matches()`改为直接字符串比较
  - 移除了相关的import语句

## 受影响的功能

### 登录验证
- 用户登录时现在使用明文密码比较
- 所有现有用户可以使用密码"1234"登录

### 用户创建
- 新创建的用户密码将直接以明文形式保存到数据库
- 建议在用户管理界面中为新用户设置默认密码"1234"

## 安全注意事项

⚠️ **重要安全提醒**：
1. 明文保存密码存在严重的安全风险
2. 任何能够访问数据库的人都可以直接看到用户密码
3. 建议仅在开发环境或特殊需求场景下使用
4. 生产环境强烈建议使用密码加密

## 数据库执行脚本

执行以下SQL来更新现有数据：
```sql
-- 更新所有用户密码为明文"1234"
UPDATE users SET password = '1234' WHERE deleted = FALSE;
```

## 默认用户账号

修改后的默认用户账号：
- admin / 1234
- manager / 1234  
- editor / 1234
- user / 1234

## 验证步骤

1. 重启应用服务
2. 执行数据库迁移脚本
3. 使用任意默认账号和密码"1234"测试登录
4. 验证新用户创建时密码保存为明文

## 修改日期
2024年12月

## 注意事项
- 保留了SecurityConfig中的PasswordEncoder Bean定义，避免Spring Boot启动时的依赖问题
- 用户表结构没有修改，仍然是VARCHAR(255)，但现在保存明文密码