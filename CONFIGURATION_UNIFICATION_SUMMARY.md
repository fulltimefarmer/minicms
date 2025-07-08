# 配置统一到bootloader模块总结

## 概述
已成功将项目中所有分散的配置文件统一到 `cms/bootloader/src/main/resources/application.yml` 中。

## 统一的配置文件
原本分散在以下位置的配置现在都统一到了bootloader模块：

### 1. 删除的配置文件
- ❌ `cms/auth-service/src/main/resources/application.yml`
- ❌ `cms/asset-service/src/main/resources/application.yml` 
- ❌ `cms/src/main/resources/application.properties`

### 2. 统一后的配置文件
- ✅ `cms/bootloader/src/main/resources/application.yml` (主配置文件)

## 配置内容整合

### 基础Spring配置
- 应用名称：`company-management-system`
- 活动环境：`dev`
- 数据源配置（PostgreSQL）
- Flyway数据库迁移配置

### 集成的服务配置

#### 1. 认证服务配置 (来自auth-service)
```yaml
auth:
  security:
    exclude-paths:
      - "/api/auth/login"
      - "/api/auth/register"
      - "/swagger-ui/**"
      - "/v3/api-docs/**"
      - "/actuator/health"
      - "/static/**"
```

#### 2. 资产服务配置 (来自asset-service)
```yaml
document:
  storage-path: files
  max-file-size: 10485760  # 10MB
  allowed-types:
    - pdf, doc, docx, xls, xlsx
    - ppt, pptx, txt
    - jpg, jpeg, png, gif, bmp
    - zip, rar
```

#### 3. MyBatis Plus配置
- 添加了 `mapper-locations: classpath:mapper/*.xml`
- 保留了驼峰命名转换和逻辑删除配置

#### 4. 服务端口配置
```yaml
services:
  bootloader:
    port: 8080
    context-path: /api
  asset-service:
    port: 8081
    context-path: /
  auth-service:
    port: 8082
    context-path: /
  user-service:
    port: 8083
    context-path: /
```

## 其他保留的配置
- Swagger/OpenAPI文档配置
- 日志配置
- 管理端点配置
- 安全配置

## 注意事项
1. 所有服务现在共享同一个配置文件
2. 不同服务的特定配置通过不同的配置节区分
3. 端口配置允许多个服务同时运行而不冲突
4. 如需添加新的服务配置，请在bootloader的application.yml中添加

## 好处
- ✅ 配置集中管理，便于维护
- ✅ 避免配置重复和不一致
- ✅ 统一的配置标准
- ✅ 更好的版本控制