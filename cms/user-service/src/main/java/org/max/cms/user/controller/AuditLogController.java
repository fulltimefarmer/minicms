package org.max.cms.user.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.max.cms.common.annotation.Auditable;
import org.max.cms.common.entity.AuditLog;
import org.max.cms.common.repository.AuditLogRepository.AuditLogQuery;
import org.max.cms.common.service.AuditLogService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 审计日志控制器
 * 提供审计日志的查询、统计等功能
 */
@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {
    
    private final AuditLogService auditLogService;
    
    /**
     * 分页查询审计日志
     * 支持多条件查询和分页
     */
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('AUDIT_LOG_READ')")
    @Auditable(
        operationType = "QUERY",
        operationName = "查询审计日志",
        operationDesc = "分页查询审计日志列表",
        businessModule = "SYSTEM",
        riskLevel = "LOW"
    )
    public ResponseEntity<IPage<AuditLog>> page(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String operationType,
            @RequestParam(required = false) String businessModule,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String riskLevel,
            @RequestParam(required = false) String ipAddress,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @RequestParam(required = false) String keyword
    ) {
        Page<AuditLog> page = new Page<>(current, size);
        
        AuditLogQuery query = new AuditLogQuery();
        query.setUserId(userId);
        query.setUsername(username);
        query.setOperationType(operationType);
        query.setBusinessModule(businessModule);
        query.setStatus(status);
        query.setRiskLevel(riskLevel);
        query.setIpAddress(ipAddress);
        query.setStartTime(startTime);
        query.setEndTime(endTime);
        query.setKeyword(keyword);
        
        IPage<AuditLog> result = auditLogService.page(page, query);
        return ResponseEntity.ok(result);
    }
    
    /**
     * 查询审计日志详情
     * 根据ID查询审计日志详细信息
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('AUDIT_LOG_READ')")
    @Auditable(
        operationType = "QUERY",
        operationName = "查看审计日志详情",
        operationDesc = "查看单条审计日志的详细信息",
        businessModule = "SYSTEM",
        targetIdExpression = "#{#id}",
        riskLevel = "LOW"
    )
    public ResponseEntity<AuditLog> getById(@PathVariable Long id) {
        AuditLog auditLog = auditLogService.getById(id);
        return ResponseEntity.ok(auditLog);
    }
    
    /**
     * 查询用户最近登录日志
     * 查询指定用户的最近登录记录
     */
    @GetMapping("/recent-logins/{userId}")
    @PreAuthorize("hasAuthority('AUDIT_LOG_READ')")
    @Auditable(
        operationType = "QUERY",
        operationName = "查询用户登录日志",
        operationDesc = "查询用户最近的登录记录",
        businessModule = "USER",
        targetType = "User",
        targetIdExpression = "#{#userId}",
        riskLevel = "LOW"
    )
    public ResponseEntity<List<AuditLog>> getRecentLoginLogs(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int limit
    ) {
        List<AuditLog> logs = auditLogService.getRecentLoginLogs(userId, limit);
        return ResponseEntity.ok(logs);
    }
    
    /**
     * 操作类型统计
     * 统计指定时间范围内各操作类型的数量分布
     */
    @GetMapping("/stats/operation-types")
    @PreAuthorize("hasAuthority('AUDIT_LOG_READ')")
    @Auditable(
        operationType = "QUERY",
        operationName = "查询操作类型统计",
        operationDesc = "统计各操作类型的数量分布",
        businessModule = "SYSTEM",
        riskLevel = "LOW"
    )
    public ResponseEntity<List<Map<String, Object>>> getOperationTypeStats(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate
    ) {
        List<Map<String, Object>> stats = auditLogService.countByOperationType(startDate, endDate);
        return ResponseEntity.ok(stats);
    }
    
    /**
     * 风险级别统计
     * 统计指定时间范围内各风险级别的数量分布
     */
    @GetMapping("/stats/risk-levels")
    @PreAuthorize("hasAuthority('AUDIT_LOG_READ')")
    @Auditable(
        operationType = "QUERY",
        operationName = "查询风险级别统计",
        operationDesc = "统计各风险级别的数量分布",
        businessModule = "SYSTEM",
        riskLevel = "LOW"
    )
    public ResponseEntity<List<Map<String, Object>>> getRiskLevelStats(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate
    ) {
        List<Map<String, Object>> stats = auditLogService.countByRiskLevel(startDate, endDate);
        return ResponseEntity.ok(stats);
    }
    
    /**
     * 每日操作量统计
     * 统计指定时间范围内每日的操作数量
     */
    @GetMapping("/stats/daily")
    @PreAuthorize("hasAuthority('AUDIT_LOG_READ')")
    @Auditable(
        operationType = "QUERY",
        operationName = "查询每日操作统计",
        operationDesc = "统计每日操作数量变化",
        businessModule = "SYSTEM",
        riskLevel = "LOW"
    )
    public ResponseEntity<List<Map<String, Object>>> getDailyStats(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate
    ) {
        List<Map<String, Object>> stats = auditLogService.countByDate(startDate, endDate);
        return ResponseEntity.ok(stats);
    }
    
    /**
     * 异常操作查询
     * 查询失败操作、高风险操作和慢操作
     */
    @GetMapping("/abnormal")
    @PreAuthorize("hasAuthority('AUDIT_LOG_READ')")
    @Auditable(
        operationType = "QUERY",
        operationName = "查询异常操作",
        operationDesc = "查询系统中的异常操作记录",
        businessModule = "SYSTEM",
        riskLevel = "MEDIUM"
    )
    public ResponseEntity<List<AuditLog>> getAbnormalLogs(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(defaultValue = "5000") long slowThreshold,
            @RequestParam(defaultValue = "50") int limit
    ) {
        List<AuditLog> logs = auditLogService.getAbnormalLogs(startDate, slowThreshold, limit);
        return ResponseEntity.ok(logs);
    }
    
    /**
     * 用户操作统计
     * 统计用户的操作情况
     */
    @GetMapping("/stats/users")
    @PreAuthorize("hasAuthority('AUDIT_LOG_READ')")
    @Auditable(
        operationType = "QUERY",
        operationName = "查询用户操作统计",
        operationDesc = "统计用户操作情况",
        businessModule = "USER",
        riskLevel = "LOW"
    )
    public ResponseEntity<List<Map<String, Object>>> getUserOperationStats(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(defaultValue = "20") int limit
    ) {
        List<Map<String, Object>> stats = auditLogService.getUserOperationStats(startDate, endDate, limit);
        return ResponseEntity.ok(stats);
    }
    
    /**
     * 清理过期日志
     * 清理指定日期之前的审计日志
     */
    @DeleteMapping("/cleanup")
    @PreAuthorize("hasAuthority('AUDIT_LOG_MANAGEMENT')")
    @Auditable(
        operationType = "DELETE",
        operationName = "清理过期审计日志",
        operationDesc = "清理系统中的过期审计日志",
        businessModule = "SYSTEM",
        riskLevel = "HIGH",
        async = false
    )
    public ResponseEntity<Map<String, Object>> cleanupExpiredLogs(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate expireDate
    ) {
        int deletedCount = auditLogService.cleanExpiredLogs(expireDate);
        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "清理完成",
            "deletedCount", deletedCount,
            "expireDate", expireDate
        ));
    }
}