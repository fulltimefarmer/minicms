package org.max.cms.common.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.max.cms.common.entity.AuditLog;
import org.max.cms.common.repository.AuditLogRepository;
import org.max.cms.common.repository.AuditLogRepository.AuditLogQuery;
import org.max.cms.common.service.AuditLogService;
import org.max.cms.common.util.AuditContextHolder;
import org.max.cms.common.util.IpUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 审计日志服务实现类
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {
    
    private final AuditLogRepository auditLogRepository;
    
    @Override
    @Transactional
    public void save(AuditLog auditLog) {
        try {
            auditLogRepository.insert(auditLog);
            log.debug("审计日志保存成功: {}", auditLog.getOperationName());
        } catch (Exception e) {
            log.error("审计日志保存失败: {}", auditLog.getOperationName(), e);
            // 审计日志保存失败不应该影响业务流程，只记录错误日志
        }
    }
    
    @Override
    @Async("auditTaskExecutor")
    public void saveAsync(AuditLog auditLog) {
        save(auditLog);
    }
    
    @Override
    @Transactional
    public void saveBatch(List<AuditLog> auditLogs) {
        if (auditLogs == null || auditLogs.isEmpty()) {
            return;
        }
        
        try {
            for (AuditLog auditLog : auditLogs) {
                auditLogRepository.insert(auditLog);
            }
            log.debug("批量保存审计日志成功，数量: {}", auditLogs.size());
        } catch (Exception e) {
            log.error("批量保存审计日志失败，数量: {}", auditLogs.size(), e);
        }
    }
    
    @Override
    public IPage<AuditLog> page(Page<AuditLog> page, AuditLogQuery query) {
        return auditLogRepository.selectAuditLogPage(page, query);
    }
    
    @Override
    public AuditLog getById(Long id) {
        return auditLogRepository.selectById(id);
    }
    
    @Override
    public List<AuditLog> getRecentLoginLogs(Long userId, int limit) {
        return auditLogRepository.selectRecentLoginLogs(userId, limit);
    }
    
    @Override
    public List<Map<String, Object>> countByOperationType(LocalDate startDate, LocalDate endDate) {
        return auditLogRepository.countByOperationType(startDate, endDate);
    }
    
    @Override
    public List<Map<String, Object>> countByRiskLevel(LocalDate startDate, LocalDate endDate) {
        return auditLogRepository.countByRiskLevel(startDate, endDate);
    }
    
    @Override
    public List<Map<String, Object>> countByDate(LocalDate startDate, LocalDate endDate) {
        return auditLogRepository.countByDate(startDate, endDate);
    }
    
    @Override
    public List<AuditLog> getAbnormalLogs(LocalDate startDate, long slowThreshold, int limit) {
        return auditLogRepository.selectAbnormalLogs(startDate, slowThreshold, limit);
    }
    
    @Override
    public List<Map<String, Object>> getUserOperationStats(LocalDate startDate, LocalDate endDate, int limit) {
        return auditLogRepository.selectUserOperationStats(startDate, endDate, limit);
    }
    
    @Override
    @Transactional
    public int cleanExpiredLogs(LocalDate expireDate) {
        int deletedCount = auditLogRepository.deleteExpiredLogs(expireDate);
        log.info("清理过期审计日志完成，删除数量: {}, 过期日期: {}", deletedCount, expireDate);
        return deletedCount;
    }
    
    @Override
    public void recordLogin(Long userId, String username, String userRealName, String ipAddress, boolean success, String errorMessage) {
        try {
            LocalDateTime now = LocalDateTime.now();
            
            AuditLog auditLog = new AuditLog()
                .setOperationType(AuditLog.OperationType.LOGIN)
                .setOperationName(success ? "用户登录成功" : "用户登录失败")
                .setOperationDesc(success ? "用户成功登录系统" : "用户登录失败: " + errorMessage)
                .setBusinessModule(AuditLog.BusinessModule.AUTH)
                .setTargetType("User")
                .setTargetId(userId != null ? userId.toString() : null)
                .setTargetName(username)
                .setUserId(userId)
                .setUsername(username)
                .setUserRealName(userRealName)
                .setIpAddress(ipAddress)
                .setStatus(success ? AuditLog.Status.SUCCESS : AuditLog.Status.FAILED)
                .setErrorMessage(errorMessage)
                .setRiskLevel(success ? AuditLog.RiskLevel.LOW : AuditLog.RiskLevel.MEDIUM)
                .setStartTime(now)
                .setEndTime(now)
                .setOperationDate(now.toLocalDate())
                .setResponseTime(0L);
            
            saveAsync(auditLog);
        } catch (Exception e) {
            log.error("记录登录审计日志失败", e);
        }
    }
    
    @Override
    public void recordLogout(Long userId, String username, String userRealName, String ipAddress) {
        try {
            LocalDateTime now = LocalDateTime.now();
            
            AuditLog auditLog = new AuditLog()
                .setOperationType(AuditLog.OperationType.LOGOUT)
                .setOperationName("用户登出")
                .setOperationDesc("用户登出系统")
                .setBusinessModule(AuditLog.BusinessModule.AUTH)
                .setTargetType("User")
                .setTargetId(userId != null ? userId.toString() : null)
                .setTargetName(username)
                .setUserId(userId)
                .setUsername(username)
                .setUserRealName(userRealName)
                .setIpAddress(ipAddress)
                .setStatus(AuditLog.Status.SUCCESS)
                .setRiskLevel(AuditLog.RiskLevel.LOW)
                .setStartTime(now)
                .setEndTime(now)
                .setOperationDate(now.toLocalDate())
                .setResponseTime(0L);
            
            saveAsync(auditLog);
        } catch (Exception e) {
            log.error("记录登出审计日志失败", e);
        }
    }
    
    @Override
    public void recordUserOperation(String operationType, String operationName, String operationDesc,
                                   String targetType, String targetId, String targetName, String riskLevel) {
        try {
            LocalDateTime now = LocalDateTime.now();
            
            // 从上下文获取当前用户信息
            Long currentUserId = AuditContextHolder.getCurrentUserId();
            String currentUsername = AuditContextHolder.getCurrentUsername();
            String currentUserRealName = AuditContextHolder.getCurrentUserRealName();
            String ipAddress = IpUtils.getClientIpAddress();
            
            AuditLog auditLog = new AuditLog()
                .setOperationType(operationType)
                .setOperationName(operationName)
                .setOperationDesc(operationDesc)
                .setBusinessModule(determineBusinessModule(targetType))
                .setTargetType(targetType)
                .setTargetId(targetId)
                .setTargetName(targetName)
                .setUserId(currentUserId)
                .setUsername(currentUsername)
                .setUserRealName(currentUserRealName)
                .setIpAddress(ipAddress)
                .setStatus(AuditLog.Status.SUCCESS)
                .setRiskLevel(riskLevel)
                .setStartTime(now)
                .setEndTime(now)
                .setOperationDate(now.toLocalDate())
                .setResponseTime(0L);
            
            saveAsync(auditLog);
        } catch (Exception e) {
            log.error("记录用户操作审计日志失败", e);
        }
    }
    
    /**
     * 根据目标类型确定业务模块
     */
    private String determineBusinessModule(String targetType) {
        if (targetType == null) {
            return AuditLog.BusinessModule.SYSTEM;
        }
        
        switch (targetType.toLowerCase()) {
            case "user":
                return AuditLog.BusinessModule.USER;
            case "asset":
                return AuditLog.BusinessModule.ASSET;
            case "role":
                return AuditLog.BusinessModule.ROLE;
            case "permission":
                return AuditLog.BusinessModule.PERMISSION;
            case "department":
                return AuditLog.BusinessModule.DEPARTMENT;
            case "document":
                return AuditLog.BusinessModule.DOCUMENT;
            default:
                return AuditLog.BusinessModule.SYSTEM;
        }
    }
}