# CMS 审计日志功能实现指南

## 概述

本文档描述了为CMS项目实现的完整审计日志功能，该功能可以自动记录所有用户操作和行为，提供全面的系统审计能力。

## 功能特性

### 🔍 自动审计记录
- **AOP切面拦截**: 使用`@Auditable`注解自动拦截方法执行
- **全面信息记录**: 记录请求参数、响应结果、用户信息、IP地址等
- **敏感数据脱敏**: 自动对密码、token等敏感字段进行脱敏处理
- **异步处理**: 支持异步记录，不影响业务性能

### 📊 丰富的查询和统计
- **多条件查询**: 支持按用户、时间、操作类型等多维度查询
- **操作统计**: 提供操作类型、风险级别、每日操作量等统计功能
- **异常监控**: 自动识别失败操作、高风险操作和慢操作
- **用户行为分析**: 统计用户操作习惯和活跃度

### 🛡️ 安全和合规
- **权限控制**: 基于RBAC模型的细粒度权限控制
- **数据完整性**: 完整记录操作前后数据变化
- **审计链路**: 支持链路追踪，便于问题排查
- **数据保护**: 支持过期数据自动清理

## 架构设计

### 核心组件

```
审计日志系统架构
├── @Auditable 注解          # 标记需要审计的方法
├── AuditAspect 切面         # AOP拦截器，自动记录日志
├── AuditLog 实体            # 审计日志数据模型
├── AuditLogService 服务     # 审计日志业务逻辑
├── AuditLogRepository 仓库  # 数据访问层
├── AuditLogController 控制器 # REST API接口
└── 工具类
    ├── AuditContextHolder   # 上下文信息管理
    └── IpUtils             # IP地址获取工具
```

### 数据模型

审计日志表 `audit_logs` 包含以下主要字段：

- **操作信息**: 操作类型、操作名称、操作描述
- **请求信息**: HTTP方法、URL、参数、请求体、响应状态
- **业务信息**: 业务模块、目标对象类型/ID/名称
- **变更信息**: 修改前后的值、变更字段列表
- **用户信息**: 用户ID、用户名、真实姓名、IP地址
- **系统信息**: 服务器名称、线程ID、链路追踪ID
- **时间信息**: 开始时间、结束时间、响应时间

## 使用方法

### 1. 基本使用

在需要审计的方法上添加 `@Auditable` 注解：

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @PostMapping
    @Auditable(
        operationType = "CREATE",
        operationName = "创建用户",
        businessModule = "USER",
        riskLevel = "MEDIUM"
    )
    public ResponseEntity<User> createUser(@RequestBody User user) {
        // 业务逻辑
        return ResponseEntity.ok(createdUser);
    }
}
```

### 2. 高级用法

#### 使用SpEL表达式动态设置信息

```java
@PutMapping("/{id}")
@Auditable(
    operationType = "UPDATE",
    operationName = "修改用户信息",
    descExpression = "修改用户: #{#user.username}",
    targetIdExpression = "#{#id}",
    targetNameExpression = "#{#user.username}",
    businessModule = "USER",
    riskLevel = "MEDIUM"
)
public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
    // 业务逻辑
    return ResponseEntity.ok(updatedUser);
}
```

#### 敏感操作配置

```java
@DeleteMapping("/{id}")
@Auditable(
    operationType = "DELETE",
    operationName = "删除用户",
    businessModule = "USER",
    riskLevel = "HIGH",
    includeResponse = true,  // 记录响应结果
    async = false,          // 同步记录确保成功
    sensitiveFields = {"password", "token"}  // 敏感字段脱敏
)
public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
    // 业务逻辑
    return ResponseEntity.ok().build();
}
```

### 3. 手动记录审计日志

对于特殊场景，可以手动调用服务记录审计日志：

```java
@Service
public class UserService {
    
    @Autowired
    private AuditLogService auditLogService;
    
    public void login(String username, String password) {
        try {
            // 登录逻辑
            User user = authenticate(username, password);
            
            // 记录登录成功
            auditLogService.recordLogin(
                user.getId(), 
                user.getUsername(), 
                user.getRealName(), 
                IpUtils.getClientIpAddress(), 
                true, 
                null
            );
        } catch (Exception e) {
            // 记录登录失败
            auditLogService.recordLogin(
                null, 
                username, 
                null, 
                IpUtils.getClientIpAddress(), 
                false, 
                e.getMessage()
            );
            throw e;
        }
    }
}
```

### 4. 查询审计日志

#### REST API查询

```bash
# 分页查询审计日志
GET /api/audit-logs/page?current=1&size=20&userId=1&operationType=CREATE

# 查询用户最近登录记录
GET /api/audit-logs/recent-logins/1?limit=10

# 查询操作类型统计
GET /api/audit-logs/stats/operation-types?startDate=2024-01-01&endDate=2024-01-31

# 查询异常操作
GET /api/audit-logs/abnormal?startDate=2024-01-01&slowThreshold=5000&limit=50
```

#### 程序查询

```java
@Service
public class ReportService {
    
    @Autowired
    private AuditLogService auditLogService;
    
    public List<AuditLog> getUserRecentActivities(Long userId) {
        AuditLogQuery query = new AuditLogQuery();
        query.setUserId(userId);
        query.setStartTime(LocalDateTime.now().minusDays(7));
        
        Page<AuditLog> page = new Page<>(1, 100);
        IPage<AuditLog> result = auditLogService.page(page, query);
        return result.getRecords();
    }
}
```

## 配置说明

### 注解参数说明

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| operationType | String | "" | 操作类型，如CREATE、UPDATE、DELETE等 |
| operationName | String | "" | 操作名称，如"创建用户" |
| operationDesc | String | "" | 操作描述 |
| businessModule | String | "" | 业务模块，如USER、ASSET等 |
| targetType | String | "" | 目标对象类型 |
| riskLevel | String | "LOW" | 风险级别：LOW、MEDIUM、HIGH、CRITICAL |
| includeParams | boolean | true | 是否记录请求参数 |
| includeRequestBody | boolean | true | 是否记录请求体 |
| includeResponse | boolean | false | 是否记录响应结果 |
| includeHeaders | boolean | false | 是否记录请求头 |
| sensitiveFields | String[] | {"password", "token", "secret", "key"} | 敏感字段列表 |
| async | boolean | true | 是否异步记录 |
| descExpression | String | "" | 操作描述的SpEL表达式 |
| targetIdExpression | String | "" | 目标ID的SpEL表达式 |
| targetNameExpression | String | "" | 目标名称的SpEL表达式 |

### 系统配置

在 `application.yml` 中可以配置相关参数：

```yaml
# 审计日志配置
audit:
  # 异步处理配置
  async:
    core-pool-size: 2
    max-pool-size: 5
    queue-capacity: 100
  
  # 数据保留配置
  retention:
    days: 365  # 日志保留天数
    
  # 性能配置
  slow-threshold: 5000  # 慢操作阈值(毫秒)
```

## 权限配置

审计日志功能需要以下权限：

```sql
-- 查看审计日志
INSERT INTO permissions (name, code, description, type) VALUES 
('查看审计日志', 'AUDIT_LOG_READ', '查看审计日志', 'API');

-- 管理审计日志
INSERT INTO permissions (name, code, description, type) VALUES 
('管理审计日志', 'AUDIT_LOG_MANAGEMENT', '管理审计日志', 'API');
```

## 性能优化

### 1. 异步处理
- 默认异步记录审计日志，不影响业务性能
- 配置专用线程池处理审计任务
- 关键操作可设置同步记录确保成功

### 2. 数据库优化
- 创建适当的索引优化查询性能
- 按日期分区存储，提高查询效率
- 定期清理过期数据，控制表大小

### 3. 存储优化
- 敏感字段自动脱敏，减少安全风险
- 大字段(如请求体、响应体)按需记录
- 使用JSON格式存储复杂数据

## 监控和运维

### 1. 异常监控

系统提供异常操作监控，自动识别：
- 操作失败的记录
- 高风险操作
- 响应时间超过阈值的慢操作

### 2. 统计报表

提供丰富的统计功能：
- 操作类型分布统计
- 风险级别分布统计
- 每日操作量趋势
- 用户操作活跃度统计

### 3. 数据清理

```bash
# 清理90天前的审计日志
DELETE /api/audit-logs/cleanup?expireDate=2024-01-01
```

## 最佳实践

### 1. 注解使用
- 在所有重要的业务操作方法上添加`@Auditable`注解
- 根据操作的重要性合理设置风险级别
- 使用SpEL表达式动态设置目标对象信息

### 2. 权限控制
- 严格控制审计日志的查看权限
- 管理功能（如清理）需要更高权限
- 定期审查权限分配

### 3. 数据管理
- 定期清理过期的审计日志
- 监控审计日志表的大小和性能
- 备份重要的审计数据

### 4. 安全考虑
- 确保审计日志不包含敏感信息
- 审计日志本身的访问也需要审计
- 防止审计日志被恶意删除或篡改

## 故障排除

### 常见问题

1. **审计日志没有记录**
   - 检查方法是否有`@Auditable`注解
   - 确认AOP配置是否正确
   - 查看是否有异常日志

2. **敏感信息泄露**
   - 检查`sensitiveFields`配置
   - 确认脱敏逻辑是否正确工作

3. **性能影响**
   - 检查是否配置了异步处理
   - 监控审计线程池状态
   - 优化数据库查询

### 调试模式

开启调试日志：

```yaml
logging:
  level:
    org.max.cms.common.aspect.AuditAspect: DEBUG
    org.max.cms.common.service.impl.AuditLogServiceImpl: DEBUG
```

## 总结

本审计日志功能提供了完整的用户操作追踪和审计能力，具有以下优势：

- **易于使用**: 简单注解即可启用审计
- **功能完整**: 涵盖记录、查询、统计、管理等全流程
- **性能优化**: 异步处理，不影响业务性能
- **安全可靠**: 敏感数据脱敏，权限严格控制
- **运维友好**: 提供丰富的监控和管理功能

通过合理使用这套审计日志功能，可以大大提升系统的安全性、合规性和可维护性。