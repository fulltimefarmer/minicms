package org.max.cms.common.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.max.cms.common.annotation.Auditable;
import org.max.cms.common.service.AuditLogService;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {
    
    private final AuditLogService auditLogService;
    private final ObjectMapper objectMapper;
    
    @Around("@annotation(auditable)")
    public Object audit(ProceedingJoinPoint joinPoint, Auditable auditable) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;
        
        String method = request != null ? request.getMethod() : "";
        String requestPath = request != null ? request.getRequestURI() : "";
        String requestParams = request != null ? request.getQueryString() : "";
        String ipAddress = getClientIpAddress(request);
        String userAgent = request != null ? request.getHeader("User-Agent") : "";
        
        Object result = null;
        String errorMessage = null;
        Integer statusCode = 200;
        
        try {
            result = joinPoint.proceed();
            return result;
        } catch (Exception e) {
            errorMessage = e.getMessage();
            statusCode = 500;
            throw e;
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            
            try {
                String requestBody = getRequestBody(joinPoint.getArgs());
                String responseBody = getResponseBody(result);
                
                // 这里应该从安全上下文获取用户信息，暂时使用占位符
                auditLogService.createAuditLog(
                        "unknown", // userId - 从安全上下文获取
                        "unknown", // username - 从安全上下文获取
                        auditable.action(),
                        auditable.resource(),
                        null, // resourceId - 从参数中提取
                        method,
                        requestPath,
                        requestParams,
                        requestBody,
                        responseBody,
                        statusCode,
                        ipAddress,
                        userAgent,
                        duration,
                        errorMessage
                );
            } catch (Exception e) {
                log.error("Failed to create audit log", e);
            }
        }
    }
    
    private String getClientIpAddress(HttpServletRequest request) {
        if (request == null) return "unknown";
        
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
    
    private String getRequestBody(Object[] args) {
        try {
            if (args != null && args.length > 0) {
                return objectMapper.writeValueAsString(Arrays.asList(args));
            }
        } catch (Exception e) {
            log.warn("Failed to serialize request body", e);
        }
        return null;
    }
    
    private String getResponseBody(Object result) {
        try {
            if (result != null) {
                return objectMapper.writeValueAsString(result);
            }
        } catch (Exception e) {
            log.warn("Failed to serialize response body", e);
        }
        return null;
    }
}