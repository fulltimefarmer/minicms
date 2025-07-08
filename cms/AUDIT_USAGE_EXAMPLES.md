# 审计日志功能使用示例

本文档提供了在CMS项目中使用审计日志功能的具体示例代码。

## 1. 用户管理模块示例

### UserController 示例

```java
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    /**
     * 创建用户
     */
    @PostMapping
    @PreAuthorize("hasAuthority('USER_CREATE')")
    @Auditable(
        operationType = "CREATE",
        operationName = "创建用户",
        operationDesc = "在系统中创建新用户账户",
        businessModule = "USER",
        targetType = "User",
        targetNameExpression = "#{#user.username}",
        riskLevel = "MEDIUM",
        includeRequestBody = true,
        includeResponse = false
    )
    public ResponseEntity<User> createUser(@RequestBody @Valid User user) {
        User createdUser = userService.createUser(user);
        return ResponseEntity.ok(createdUser);
    }
    
    /**
     * 更新用户信息
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_UPDATE')")
    @Auditable(
        operationType = "UPDATE",
        operationName = "修改用户信息",
        descExpression = "修改用户: #{#user.username} 的个人信息",
        businessModule = "USER",
        targetType = "User",
        targetIdExpression = "#{#id}",
        targetNameExpression = "#{#user.username}",
        riskLevel = "MEDIUM"
    )
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody @Valid User user) {
        User updatedUser = userService.updateUser(id, user);
        return ResponseEntity.ok(updatedUser);
    }
    
    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_DELETE')")
    @Auditable(
        operationType = "DELETE",
        operationName = "删除用户",
        descExpression = "删除用户ID: #{#id}",
        businessModule = "USER",
        targetType = "User",
        targetIdExpression = "#{#id}",
        riskLevel = "HIGH",
        async = false  // 同步记录确保成功
    )
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }
    
    /**
     * 重置用户密码
     */
    @PostMapping("/{id}/reset-password")
    @PreAuthorize("hasAuthority('USER_RESET_PASSWORD')")
    @Auditable(
        operationType = "UPDATE",
        operationName = "重置用户密码",
        descExpression = "管理员重置用户密码",
        businessModule = "USER",
        targetType = "User",
        targetIdExpression = "#{#id}",
        riskLevel = "HIGH",
        includeRequestBody = false,  // 不记录密码信息
        sensitiveFields = {"password", "newPassword", "oldPassword"}
    )
    public ResponseEntity<Void> resetPassword(@PathVariable Long id, @RequestBody PasswordResetRequest request) {
        userService.resetPassword(id, request.getNewPassword());
        return ResponseEntity.ok().build();
    }
}
```

### AuthController 示例

```java
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    private final AuditLogService auditLogService;
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    @Auditable(
        operationType = "LOGIN",
        operationName = "用户登录",
        businessModule = "AUTH",
        targetType = "User",
        targetNameExpression = "#{#loginRequest.username}",
        riskLevel = "LOW",
        includeRequestBody = false,  // 不记录密码
        sensitiveFields = {"password"}
    )
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
        try {
            LoginResponse response = authService.login(loginRequest);
            
            // 手动记录登录成功
            auditLogService.recordLogin(
                response.getUserId(),
                response.getUsername(),
                response.getUserRealName(),
                IpUtils.getClientIpAddress(),
                true,
                null
            );
            
            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            // 手动记录登录失败
            auditLogService.recordLogin(
                null,
                loginRequest.getUsername(),
                null,
                IpUtils.getClientIpAddress(),
                false,
                e.getMessage()
            );
            throw e;
        }
    }
    
    /**
     * 用户登出
     */
    @PostMapping("/logout")
    @Auditable(
        operationType = "LOGOUT",
        operationName = "用户登出",
        businessModule = "AUTH",
        riskLevel = "LOW"
    )
    public ResponseEntity<Void> logout() {
        // 获取当前用户信息
        Long userId = AuditContextHolder.getCurrentUserId();
        String username = AuditContextHolder.getCurrentUsername();
        String userRealName = AuditContextHolder.getCurrentUserRealName();
        
        authService.logout();
        
        // 手动记录登出
        auditLogService.recordLogout(
            userId,
            username,
            userRealName,
            IpUtils.getClientIpAddress()
        );
        
        return ResponseEntity.ok().build();
    }
}
```

## 2. 资产管理模块示例

### AssetController 示例

```java
@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
public class AssetController {
    
    private final AssetService assetService;
    
    /**
     * 创建资产
     */
    @PostMapping
    @PreAuthorize("hasAuthority('ASSET_CREATE')")
    @Auditable(
        operationType = "CREATE",
        operationName = "登记新资产",
        descExpression = "登记资产: #{#asset.name}",
        businessModule = "ASSET",
        targetType = "Asset",
        targetNameExpression = "#{#asset.name}",
        riskLevel = "MEDIUM"
    )
    public ResponseEntity<Asset> createAsset(@RequestBody @Valid Asset asset) {
        Asset createdAsset = assetService.createAsset(asset);
        return ResponseEntity.ok(createdAsset);
    }
    
    /**
     * 资产分配
     */
    @PostMapping("/{id}/assign")
    @PreAuthorize("hasAuthority('ASSET_ASSIGN')")
    @Auditable(
        operationType = "UPDATE",
        operationName = "分配资产",
        descExpression = "将资产分配给用户: #{#assignRequest.userId}",
        businessModule = "ASSET",
        targetType = "Asset",
        targetIdExpression = "#{#id}",
        riskLevel = "MEDIUM"
    )
    public ResponseEntity<Void> assignAsset(@PathVariable Long id, @RequestBody AssetAssignRequest assignRequest) {
        assetService.assignAsset(id, assignRequest.getUserId());
        return ResponseEntity.ok().build();
    }
    
    /**
     * 资产报废
     */
    @PostMapping("/{id}/dispose")
    @PreAuthorize("hasAuthority('ASSET_DISPOSE')")
    @Auditable(
        operationType = "UPDATE",
        operationName = "资产报废",
        descExpression = "将资产设置为报废状态",
        businessModule = "ASSET",
        targetType = "Asset",
        targetIdExpression = "#{#id}",
        riskLevel = "HIGH",
        async = false
    )
    public ResponseEntity<Void> disposeAsset(@PathVariable Long id, @RequestBody AssetDisposeRequest disposeRequest) {
        assetService.disposeAsset(id, disposeRequest.getReason());
        return ResponseEntity.ok().build();
    }
    
    /**
     * 批量导入资产
     */
    @PostMapping("/batch-import")
    @PreAuthorize("hasAuthority('ASSET_IMPORT')")
    @Auditable(
        operationType = "IMPORT",
        operationName = "批量导入资产",
        operationDesc = "从Excel文件批量导入资产信息",
        businessModule = "ASSET",
        riskLevel = "HIGH",
        includeRequestBody = false,  // 文件上传不记录内容
        async = false
    )
    public ResponseEntity<BatchImportResult> batchImport(@RequestParam("file") MultipartFile file) {
        BatchImportResult result = assetService.batchImport(file);
        return ResponseEntity.ok(result);
    }
}
```

## 3. 权限管理模块示例

### RoleController 示例

```java
@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {
    
    private final RoleService roleService;
    
    /**
     * 分配角色权限
     */
    @PostMapping("/{roleId}/permissions")
    @PreAuthorize("hasAuthority('ROLE_PERMISSION_ASSIGN')")
    @Auditable(
        operationType = "UPDATE",
        operationName = "分配角色权限",
        descExpression = "为角色分配权限",
        businessModule = "ROLE",
        targetType = "Role",
        targetIdExpression = "#{#roleId}",
        riskLevel = "CRITICAL",  // 权限操作风险级别高
        includeRequestBody = true,
        async = false  // 同步记录确保成功
    )
    public ResponseEntity<Void> assignPermissions(@PathVariable Long roleId, @RequestBody List<Long> permissionIds) {
        roleService.assignPermissions(roleId, permissionIds);
        return ResponseEntity.ok().build();
    }
    
    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_DELETE')")
    @Auditable(
        operationType = "DELETE",
        operationName = "删除角色",
        descExpression = "删除系统角色",
        businessModule = "ROLE",
        targetType = "Role",
        targetIdExpression = "#{#id}",
        riskLevel = "CRITICAL",
        async = false
    )
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.ok().build();
    }
}
```

## 4. 业务服务层示例

### UserService 示例

```java
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;
    
    /**
     * 启用/禁用用户
     */
    @Auditable(
        operationType = "UPDATE",
        operationName = "切换用户状态",
        descExpression = "#{#enabled ? '启用' : '禁用'}用户",
        businessModule = "USER",
        targetType = "User",
        targetIdExpression = "#{#userId}",
        riskLevel = "HIGH"
    )
    public void toggleUserStatus(Long userId, boolean enabled) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("用户不存在"));
        
        boolean oldStatus = user.getEnabled();
        user.setEnabled(enabled);
        userRepository.save(user);
        
        // 记录状态变更的详细信息
        auditLogService.recordUserOperation(
            "UPDATE",
            enabled ? "启用用户" : "禁用用户",
            String.format("用户状态从 %s 变更为 %s", oldStatus ? "启用" : "禁用", enabled ? "启用" : "禁用"),
            "User",
            userId.toString(),
            user.getUsername(),
            "HIGH"
        );
    }
    
    /**
     * 批量操作用户
     */
    public void batchUpdateUsers(List<Long> userIds, UserBatchUpdateRequest request) {
        for (Long userId : userIds) {
            updateUser(userId, request);
        }
        
        // 记录批量操作
        auditLogService.recordUserOperation(
            "UPDATE",
            "批量更新用户",
            String.format("批量更新了 %d 个用户", userIds.size()),
            "User",
            userIds.toString(),
            "批量操作",
            "HIGH"
        );
    }
}
```

## 5. 自定义审计记录示例

### 复杂业务场景审计

```java
@Service
@RequiredArgsConstructor
public class WorkflowService {
    
    private final AuditLogService auditLogService;
    
    /**
     * 工作流审批
     */
    public void approveWorkflow(Long workflowId, String approverComment, boolean approved) {
        // 业务逻辑...
        
        // 自定义审计记录
        LocalDateTime now = LocalDateTime.now();
        
        AuditLog auditLog = new AuditLog()
            .setOperationType(approved ? "APPROVE" : "REJECT")
            .setOperationName(approved ? "审批通过" : "审批拒绝")
            .setOperationDesc(String.format("工作流审批: %s, 意见: %s", approved ? "通过" : "拒绝", approverComment))
            .setBusinessModule("WORKFLOW")
            .setTargetType("Workflow")
            .setTargetId(workflowId.toString())
            .setTargetName("工作流审批")
            .setUserId(AuditContextHolder.getCurrentUserId())
            .setUsername(AuditContextHolder.getCurrentUsername())
            .setUserRealName(AuditContextHolder.getCurrentUserRealName())
            .setIpAddress(IpUtils.getClientIpAddress())
            .setStatus(AuditLog.Status.SUCCESS)
            .setRiskLevel(AuditLog.RiskLevel.MEDIUM)
            .setStartTime(now)
            .setEndTime(now)
            .setOperationDate(now.toLocalDate());
        
        auditLogService.saveAsync(auditLog);
    }
}
```

## 6. 定时任务审计示例

### 系统维护任务审计

```java
@Component
@RequiredArgsConstructor
public class MaintenanceTask {
    
    private final AuditLogService auditLogService;
    
    @Scheduled(cron = "0 0 2 * * ?")  // 每天凌晨2点执行
    public void cleanupTempFiles() {
        long startTime = System.currentTimeMillis();
        LocalDateTime startDateTime = LocalDateTime.now();
        
        try {
            // 执行清理逻辑
            int deletedCount = performCleanup();
            
            // 记录成功的维护操作
            recordMaintenanceAudit(
                "CLEANUP",
                "清理临时文件",
                String.format("成功清理了 %d 个临时文件", deletedCount),
                AuditLog.Status.SUCCESS,
                null,
                startDateTime,
                System.currentTimeMillis() - startTime
            );
            
        } catch (Exception e) {
            // 记录失败的维护操作
            recordMaintenanceAudit(
                "CLEANUP",
                "清理临时文件",
                "临时文件清理失败",
                AuditLog.Status.FAILED,
                e.getMessage(),
                startDateTime,
                System.currentTimeMillis() - startTime
            );
        }
    }
    
    private void recordMaintenanceAudit(String operationType, String operationName, String operationDesc,
                                      String status, String errorMessage, LocalDateTime startTime, long responseTime) {
        AuditLog auditLog = new AuditLog()
            .setOperationType(operationType)
            .setOperationName(operationName)
            .setOperationDesc(operationDesc)
            .setBusinessModule("SYSTEM")
            .setTargetType("System")
            .setTargetName("系统维护")
            .setUserId(null)  // 系统任务没有用户
            .setUsername("SYSTEM")
            .setUserRealName("系统任务")
            .setIpAddress("127.0.0.1")
            .setStatus(status)
            .setErrorMessage(errorMessage)
            .setRiskLevel(AuditLog.RiskLevel.LOW)
            .setStartTime(startTime)
            .setEndTime(LocalDateTime.now())
            .setOperationDate(startTime.toLocalDate())
            .setResponseTime(responseTime);
        
        auditLogService.save(auditLog);  // 系统任务使用同步记录
    }
}
```

## 7. 审计日志查询示例

### 报表服务示例

```java
@Service
@RequiredArgsConstructor
public class AuditReportService {
    
    private final AuditLogService auditLogService;
    
    /**
     * 生成用户活动报告
     */
    public UserActivityReport generateUserActivityReport(Long userId, LocalDate startDate, LocalDate endDate) {
        // 查询用户操作统计
        List<Map<String, Object>> stats = auditLogService.getUserOperationStats(startDate, endDate, 1);
        
        // 查询最近登录记录
        List<AuditLog> recentLogins = auditLogService.getRecentLoginLogs(userId, 10);
        
        // 查询异常操作
        List<AuditLog> abnormalLogs = auditLogService.getAbnormalLogs(startDate, 5000, 20);
        
        return UserActivityReport.builder()
            .userId(userId)
            .reportPeriod(startDate + " 至 " + endDate)
            .operationStats(stats)
            .recentLogins(recentLogins)
            .abnormalOperations(abnormalLogs)
            .build();
    }
    
    /**
     * 生成系统安全报告
     */
    public SecurityReport generateSecurityReport(LocalDate startDate, LocalDate endDate) {
        // 统计高风险操作
        AuditLogQuery query = new AuditLogQuery();
        query.setRiskLevel("HIGH");
        query.setStartTime(startDate.atStartOfDay());
        query.setEndTime(endDate.atTime(23, 59, 59));
        
        Page<AuditLog> page = new Page<>(1, 100);
        IPage<AuditLog> highRiskOps = auditLogService.page(page, query);
        
        // 统计失败操作
        query.setRiskLevel(null);
        query.setStatus("FAILED");
        IPage<AuditLog> failedOps = auditLogService.page(page, query);
        
        // 统计操作类型分布
        List<Map<String, Object>> operationTypeStats = auditLogService.countByOperationType(startDate, endDate);
        
        return SecurityReport.builder()
            .reportPeriod(startDate + " 至 " + endDate)
            .highRiskOperations(highRiskOps.getRecords())
            .failedOperations(failedOps.getRecords())
            .operationTypeDistribution(operationTypeStats)
            .build();
    }
}
```

## 8. 前端集成示例

### JavaScript 调用示例

```javascript
// 查询审计日志
async function queryAuditLogs(params) {
    try {
        const response = await fetch('/api/audit-logs/page?' + new URLSearchParams(params), {
            headers: {
                'Authorization': 'Bearer ' + getToken()
            }
        });
        
        if (!response.ok) {
            throw new Error('查询失败');
        }
        
        const data = await response.json();
        return data;
    } catch (error) {
        console.error('查询审计日志失败:', error);
        throw error;
    }
}

// 查询操作统计
async function getOperationStats(startDate, endDate) {
    try {
        const response = await fetch(`/api/audit-logs/stats/operation-types?startDate=${startDate}&endDate=${endDate}`, {
            headers: {
                'Authorization': 'Bearer ' + getToken()
            }
        });
        
        const stats = await response.json();
        
        // 渲染图表
        renderOperationChart(stats);
        
        return stats;
    } catch (error) {
        console.error('获取操作统计失败:', error);
    }
}

// 实时监控异常操作
function monitorAbnormalOperations() {
    setInterval(async () => {
        try {
            const today = new Date().toISOString().split('T')[0];
            const response = await fetch(`/api/audit-logs/abnormal?startDate=${today}&limit=10`, {
                headers: {
                    'Authorization': 'Bearer ' + getToken()
                }
            });
            
            const abnormalLogs = await response.json();
            
            if (abnormalLogs.length > 0) {
                showAbnormalAlert(abnormalLogs);
            }
        } catch (error) {
            console.error('监控异常操作失败:', error);
        }
    }, 30000); // 每30秒检查一次
}
```

这些示例展示了如何在不同的场景中使用审计日志功能，包括：

- 基本的CRUD操作审计
- 敏感操作的特殊处理
- 手动记录特殊业务场景
- 定时任务的审计记录
- 复杂查询和报表生成
- 前端集成和实时监控

通过这些示例，您可以根据具体的业务需求灵活地应用审计日志功能。