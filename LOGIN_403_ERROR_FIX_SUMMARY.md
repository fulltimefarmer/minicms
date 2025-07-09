# POST /api/auth/login 403错误修复总结

## 问题描述

客户端向 `POST /api/auth/login` 发送登录请求时，虽然JWT过滤器正确识别了该路径应该被排除在认证之外，但最终仍然返回403错误。

## 根本原因分析

通过分析后端日志发现了路径不匹配的问题：

1. **Context Path配置冲突**：
   - 应用配置了 `server.servlet.context-path: /api`
   - 客户端请求：`POST /api/auth/login`
   - JWT过滤器获取完整URI：`/api/auth/login`
   - Spring Security处理的路径：`/auth/login`（去除了context path）

2. **路径匹配不一致**：
   - 配置中的排除路径包含：`/api/auth/login`
   - 但Spring Security实际匹配的是：`/auth/login`
   - 导致路径匹配失败，请求被拒绝

3. **异常处理缺失**：
   - 没有配置适当的AuthenticationEntryPoint
   - 请求被重定向到 `/error` 端点
   - 最终触发 `Http403ForbiddenEntryPoint`

## 解决方案

### 1. 修复JWT过滤器路径获取逻辑

**文件**: `cms/auth-service/src/main/java/org/max/cms/auth/filter/JwtAuthenticationFilter.java`

**修改**:
```java
// 修改前
String requestPath = request.getRequestURI();

// 修改后
// 使用getServletPath()获取去除context path后的路径，确保与Spring Security的路径匹配逻辑一致
String requestPath = request.getServletPath();
```

**说明**: 使用 `getServletPath()` 替代 `getRequestURI()`，这样可以获取去除context path后的真实servlet路径，确保与Spring Security的路径匹配逻辑一致。

### 2. 修复路径匹配逻辑

**文件**: `cms/auth-service/src/main/java/org/max/cms/auth/config/SecurityPathsConfig.java`

**修改**:
```java
// 修改前
boolean matches = path.equals(excludePath) || path.startsWith(excludePath);

// 修改后
boolean matches = path.equals(excludePath);
```

**说明**: 移除 `startsWith` 检查，只使用精确匹配，避免误匹配问题。

### 3. 添加异常处理配置

**文件**: `cms/auth-service/src/main/java/org/max/cms/auth/config/SecurityConfig.java`

**修改**: 在SecurityFilterChain配置中添加异常处理：
```java
.exceptionHandling(exceptions -> exceptions
        .authenticationEntryPoint((request, response, authException) -> {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Unauthorized access\"}");
        })
        .accessDeniedHandler((request, response, accessDeniedException) -> {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Access denied\"}");
        })
)
```

**说明**: 为未认证的请求返回401状态码而不是重定向到错误页面，提供更清晰的错误响应。

## 配置验证

确保 `application.yml` 中的排除路径配置正确：

```yaml
auth:
  security:
    exclude-paths:
      - "/auth/login"        # 对应servlet path
      - "/auth/register"     # 对应servlet path
      - "/api/auth/login"    # 对应完整URI（向后兼容）
      - "/api/auth/register" # 对应完整URI（向后兼容）
      # ... 其他路径
```

## 预期结果

修复后的行为：

1. 客户端请求 `POST /api/auth/login`
2. JWT过滤器获取servlet路径：`/auth/login`
3. 路径匹配成功，排除JWT认证
4. 请求正常处理，不再返回403错误
5. 登录成功后返回正确的JWT token

## 测试建议

1. **功能测试**: 使用Postman或curl测试登录端点
2. **日志验证**: 检查日志确认路径匹配成功
3. **端到端测试**: 验证完整的登录流程

```bash
# 测试命令示例
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

## 相关文件

- `cms/auth-service/src/main/java/org/max/cms/auth/filter/JwtAuthenticationFilter.java`
- `cms/auth-service/src/main/java/org/max/cms/auth/config/SecurityPathsConfig.java`
- `cms/auth-service/src/main/java/org/max/cms/auth/config/SecurityConfig.java`
- `cms/bootloader/src/main/resources/application.yml`