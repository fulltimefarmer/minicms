package org.max.cms.common.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.max.cms.common.entity.AuditLog;
import org.max.cms.common.repository.AuditLogRepository.AuditLogQuery;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 审计日志服务接口
 */
public interface AuditLogService {
    
    /**
     * 保存审计日志
     */
    void save(AuditLog auditLog);
    
    /**
     * 异步保存审计日志
     */
    void saveAsync(AuditLog auditLog);
    
    /**
     * 批量保存审计日志
     */
    void saveBatch(List<AuditLog> auditLogs);
    
    /**
     * 分页查询审计日志
     */
    IPage<AuditLog> page(Page<AuditLog> page, AuditLogQuery query);
    
    /**
     * 根据ID查询审计日志
     */
    AuditLog getById(Long id);
    
    /**
     * 查询用户最近的登录日志
     */
    List<AuditLog> getRecentLoginLogs(Long userId, int limit);
    
    /**
     * 统计操作类型分布
     */
    List<Map<String, Object>> countByOperationType(LocalDate startDate, LocalDate endDate);
    
    /**
     * 统计风险级别分布
     */
    List<Map<String, Object>> countByRiskLevel(LocalDate startDate, LocalDate endDate);
    
    /**
     * 统计每日操作数量
     */
    List<Map<String, Object>> countByDate(LocalDate startDate, LocalDate endDate);
    
    /**
     * 查询异常操作日志
     */
    List<AuditLog> getAbnormalLogs(LocalDate startDate, long slowThreshold, int limit);
    
    /**
     * 查询用户操作统计
     */
    List<Map<String, Object>> getUserOperationStats(LocalDate startDate, LocalDate endDate, int limit);
    
    /**
     * 清理过期的审计日志
     */
    int cleanExpiredLogs(LocalDate expireDate);
    
    /**
     * 记录登录操作
     */
    void recordLogin(Long userId, String username, String userRealName, String ipAddress, boolean success, String errorMessage);
    
    /**
     * 记录登出操作
     */
    void recordLogout(Long userId, String username, String userRealName, String ipAddress);
    
    /**
     * 记录用户操作
     */
    void recordUserOperation(String operationType, String operationName, String operationDesc,
                           String targetType, String targetId, String targetName, String riskLevel);
}