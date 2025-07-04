package org.max.cms.common.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.max.cms.common.entity.AuditLog;
import org.max.cms.common.repository.AuditLogRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogService {
    
    private final AuditLogRepository auditLogRepository;
    
    @Async
    public void saveAuditLog(AuditLog auditLog) {
        try {
            auditLogRepository.insert(auditLog);
        } catch (Exception e) {
            log.error("Failed to save audit log", e);
        }
    }
    
    public Page<AuditLog> getAuditLogs(int page, int size, String userId, String action, 
                                       LocalDateTime startTime, LocalDateTime endTime) {
        QueryWrapper<AuditLog> queryWrapper = new QueryWrapper<>();
        
        if (userId != null && !userId.isEmpty()) {
            queryWrapper.eq("user_id", userId);
        }
        if (action != null && !action.isEmpty()) {
            queryWrapper.eq("action", action);
        }
        if (startTime != null) {
            queryWrapper.ge("created_at", startTime);
        }
        if (endTime != null) {
            queryWrapper.le("created_at", endTime);
        }
        
        queryWrapper.orderByDesc("created_at");
        
        return auditLogRepository.selectPage(new Page<>(page, size), queryWrapper);
    }
    
    public void createAuditLog(String userId, String username, String action, String resource, 
                              String resourceId, String method, String requestPath, 
                              String requestParams, String requestBody, String responseBody, 
                              Integer statusCode, String ipAddress, String userAgent, 
                              Long duration, String errorMessage) {
        
        AuditLog auditLog = AuditLog.builder()
                .userId(userId)
                .username(username)
                .action(action)
                .resource(resource)
                .resourceId(resourceId)
                .method(method)
                .requestPath(requestPath)
                .requestParams(requestParams)
                .requestBody(requestBody)
                .responseBody(responseBody)
                .statusCode(statusCode)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .duration(duration)
                .errorMessage(errorMessage)
                .build();
                
        saveAuditLog(auditLog);
    }
}