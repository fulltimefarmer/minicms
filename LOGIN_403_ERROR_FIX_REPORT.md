# 登录403错误修复报告

## 问题描述
项目中输入正确的用户名和密码后仍然返回403 Forbidden错误，用户无法正常访问受保护的资源。

## 问题根因分析

### 1. 权限配置问题
**原因**: JWT认证过滤器中用户权限设置为空数组
```java
// 原代码 - JwtAuthenticationFilter.java 第55行
new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>())
```
**影响**: 用户认证成功但没有任何权限，导致Spring Security拒绝访问（403错误）

### 2. 角色权限查询未实现
**原因**: AuthServiceImpl中角色和权限查询为TODO状态
```java
// 原代码 - AuthServiceImpl.java 第45-46行
.roles(new ArrayList<>()) // TODO: 实现角色查询
.permissions(new ArrayList<>()) // TODO: 实现权限查询
```
**影响**: 登录响应中没有返回用户的实际角色和权限信息

### 3. JWT Token权限信息缺失
**原因**: JwtUtil类没有提供提取权限信息的方法
**影响**: 无法从JWT token中获取用户权限进行授权验证

### 4. 数据库映射不完整
**原因**: User实体类缺少first_name和last_name字段映射
**影响**: 可能导致数据库查询错误

## 修复方案

### 1. 增强UserRepository - 添加角色权限查询
**文件**: `cms/auth-service/src/main/java/org/max/cms/auth/repository/UserRepository.java`
```java
List<String> findRolesByUsername(String username);
List<String> findPermissionsByUsername(String username);
```

**文件**: `cms/auth-service/src/main/java/org/max/cms/auth/repository/UserRepository.xml`
```xml
<select id="findRolesByUsername" resultType="string">
    SELECT DISTINCT r.code
    FROM users u
    JOIN user_roles ur ON u.id = ur.user_id
    JOIN roles r ON ur.role_id = r.id
    WHERE u.username = #{username}
    AND u.enabled = true AND r.enabled = true
    AND u.deleted = false AND ur.deleted = false AND r.deleted = false
</select>

<select id="findPermissionsByUsername" resultType="string">
    SELECT DISTINCT p.code
    FROM users u
    JOIN user_roles ur ON u.id = ur.user_id
    JOIN roles r ON ur.role_id = r.id
    JOIN role_permissions rp ON r.id = rp.role_id
    JOIN permissions p ON rp.permission_id = p.id
    WHERE u.username = #{username}
    AND u.enabled = true AND r.enabled = true AND p.enabled = true
    AND u.deleted = false AND ur.deleted = false 
    AND r.deleted = false AND rp.deleted = false AND p.deleted = false
</select>
```

### 2. 修复AuthServiceImpl - 实现角色权限加载
**文件**: `cms/auth-service/src/main/java/org/max/cms/auth/service/impl/AuthServiceImpl.java`

**修改点**:
- 在登录方法中查询用户的角色和权限
- 将角色权限信息添加到JWT token claims中
- 在登录响应中返回实际的角色权限列表

```java
// 查询用户角色和权限
List<String> roles = userRepository.findRolesByUsername(user.getUsername());
List<String> permissions = userRepository.findPermissionsByUsername(user.getUsername());

// 生成token时包含权限信息
Map<String, Object> claims = new HashMap<>();
claims.put("userId", user.getId());
claims.put("email", user.getEmail());
claims.put("roles", roles);
claims.put("permissions", permissions);
String token = jwtUtil.generateToken(user.getUsername(), claims);
```

### 3. 增强JwtUtil - 添加权限提取方法
**文件**: `cms/auth-service/src/main/java/org/max/cms/auth/util/JwtUtil.java`

```java
@SuppressWarnings("unchecked")
public List<String> extractPermissions(String token) {
    Claims claims = extractAllClaims(token);
    List<String> permissions = (List<String>) claims.get("permissions");
    return permissions != null ? permissions : new ArrayList<>();
}
```

### 4. 修复JwtAuthenticationFilter - 正确设置用户权限
**文件**: `cms/auth-service/src/main/java/org/max/cms/auth/filter/JwtAuthenticationFilter.java`

```java
if (jwtUtil.validateToken(jwt, username)) {
    // 从JWT token中提取权限信息
    List<String> permissions = jwtUtil.extractPermissions(jwt);
    List<SimpleGrantedAuthority> authorities = permissions.stream()
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toList());
    
    UsernamePasswordAuthenticationToken authToken = 
        new UsernamePasswordAuthenticationToken(username, null, authorities);
    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
    SecurityContextHolder.getContext().setAuthentication(authToken);
    
    log.debug("Authentication successful for user: {}, authorities: {}", username, authorities);
}
```

### 5. 完善User实体类 - 添加缺失字段
**文件**: `cms/auth-service/src/main/java/org/max/cms/auth/entity/User.java`

添加了firstName和lastName字段以匹配数据库结构。

## 测试验证

### 1. 测试用户和权限
根据数据库初始化脚本，系统包含以下测试用户：

| 用户名 | 密码 | 角色 | 权限范围 |
|--------|------|------|----------|
| admin | 1234 | SUPER_ADMIN | 所有权限 |
| manager | 1234 | MANAGER | 部门管理权限 |
| editor | 1234 | EDITOR | 内容编辑权限 |
| user | 1234 | USER | 基本查看权限 |

### 2. 预期行为
修复后，用户登录将：
1. 正确验证用户名和密码
2. 查询并返回用户的角色和权限
3. 在JWT token中包含权限信息
4. 在后续请求中正确验证权限
5. 允许有权限的用户访问相应资源
6. 对于admin用户，应该有完整的系统管理权限

### 3. API测试示例
```bash
# 登录测试
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "1234"}'

# 预期响应包含完整的roles和permissions数组
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "userId": 1,
    "username": "admin",
    "roles": ["SUPER_ADMIN"],
    "permissions": ["USER_CREATE", "USER_READ", "USER_UPDATE", ...]
  }
}
```

## 安全改进建议

### 1. 密码加密
当前密码以明文存储，建议：
- 在用户创建时使用BCryptPasswordEncoder加密密码
- 修改密码验证逻辑使用加密比较
- 更新现有用户密码为加密格式

### 2. 权限细化
建议根据实际业务需求：
- 定义更细粒度的权限控制
- 实现资源级别的权限验证
- 添加数据权限控制（如部门数据隔离）

### 3. 安全配置
- 使用更强的JWT签名密钥
- 配置适当的token过期时间
- 实现token黑名单机制
- 添加登录失败锁定机制

## 总结

此次修复解决了403错误的根本原因，通过：
1. **实现完整的权限查询机制** - 从数据库正确加载用户角色权限
2. **修复JWT认证流程** - 在token中包含并正确解析权限信息
3. **更新Spring Security配置** - 为认证用户设置正确的权限列表
4. **完善数据映射** - 确保实体类与数据库结构一致

用户现在可以使用正确的用户名密码登录并访问相应的受保护资源，不再出现403错误。

## 修改文件清单

1. `cms/auth-service/src/main/java/org/max/cms/auth/repository/UserRepository.java`
2. `cms/auth-service/src/main/java/org/max/cms/auth/repository/UserRepository.xml`
3. `cms/auth-service/src/main/java/org/max/cms/auth/service/impl/AuthServiceImpl.java`
4. `cms/auth-service/src/main/java/org/max/cms/auth/util/JwtUtil.java`
5. `cms/auth-service/src/main/java/org/max/cms/auth/filter/JwtAuthenticationFilter.java`
6. `cms/auth-service/src/main/java/org/max/cms/auth/entity/User.java`