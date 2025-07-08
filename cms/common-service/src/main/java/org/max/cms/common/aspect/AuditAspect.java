package org.max.cms.common.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.max.cms.common.annotation.Auditable;
import org.max.cms.common.entity.AuditLog;
import org.max.cms.common.service.AuditLogService;
import org.max.cms.common.util.AuditContextHolder;
import org.max.cms.common.util.IpUtils;
import org.springframework.core.annotation.Order;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.InetAddress;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 审计切面
 * 拦截带有@Auditable注解的方法，自动记录操作日志
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
@Order(1) // 确保在事务切面之后执行
public class AuditAspect {
    
    private final AuditLogService auditLogService;
    private final ObjectMapper objectMapper;
    private final SpelExpressionParser parser = new SpelExpressionParser();
    
    @Around("@annotation(auditable)")
    public Object audit(ProceedingJoinPoint joinPoint, Auditable auditable) throws Throwable {
        // 记录开始时间
        long startTime = System.currentTimeMillis();
        LocalDateTime startDateTime = LocalDateTime.now();
        
        // 构建基础审计日志对象
        AuditLog auditLog = buildBaseAuditLog(joinPoint, auditable, startDateTime);
        
        Object result = null;
        Exception exception = null;
        
        try {
            // 执行目标方法
            result = joinPoint.proceed();
            
            // 处理成功情况
            auditLog.setStatus(AuditLog.Status.SUCCESS);
            if (auditable.includeResponse() && result != null) {
                auditLog.setResponseBody(toJsonString(maskSensitiveData(result, auditable.sensitiveFields())));
            }
            
            return result;
            
        } catch (Exception e) {
            exception = e;
            // 处理异常情况
            auditLog.setStatus(AuditLog.Status.FAILED);
            auditLog.setErrorMessage(e.getMessage());
            
            throw e;
            
        } finally {
            // 完成审计日志记录
            completeAuditLog(auditLog, startTime, joinPoint, auditable, result, exception);
            
            // 记录审计日志
            if (auditable.async()) {
                auditLogService.saveAsync(auditLog);
            } else {
                auditLogService.save(auditLog);
            }
        }
    }
    
    /**
     * 构建基础审计日志对象
     */
    private AuditLog buildBaseAuditLog(ProceedingJoinPoint joinPoint, Auditable auditable, LocalDateTime startTime) {
        AuditLog auditLog = new AuditLog();
        
        // 设置时间信息
        auditLog.setStartTime(startTime);
        auditLog.setOperationDate(startTime.toLocalDate());
        
        // 设置操作基本信息
        auditLog.setOperationType(determineOperationType(auditable));
        auditLog.setOperationName(auditable.operationName());
        auditLog.setOperationDesc(evaluateExpression(auditable.descExpression(), joinPoint, auditable.operationDesc()));
        
        // 设置业务信息
        auditLog.setBusinessModule(auditable.businessModule());
        auditLog.setTargetType(auditable.targetType());
        auditLog.setRiskLevel(auditable.riskLevel());
        
        // 处理目标对象信息
        processTargetInfo(auditLog, joinPoint, auditable);
        
        // 设置HTTP请求信息
        setRequestInfo(auditLog, auditable);
        
        // 设置用户和环境信息
        setUserAndEnvironmentInfo(auditLog);
        
        // 设置系统信息
        setSystemInfo(auditLog);
        
        return auditLog;
    }
    
    /**
     * 完成审计日志记录
     */
    private void completeAuditLog(AuditLog auditLog, long startTime, ProceedingJoinPoint joinPoint, 
                                 Auditable auditable, Object result, Exception exception) {
        // 设置结束时间和响应时间
        LocalDateTime endTime = LocalDateTime.now();
        auditLog.setEndTime(endTime);
        auditLog.setResponseTime(System.currentTimeMillis() - startTime);
        
        // 设置HTTP响应状态
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            if (exception != null) {
                auditLog.setResponseStatus(500);
            } else {
                auditLog.setResponseStatus(200);
            }
        }
    }
    
    /**
     * 确定操作类型
     */
    private String determineOperationType(Auditable auditable) {
        if (StringUtils.hasText(auditable.operationType())) {
            return auditable.operationType();
        }
        
        // 从HTTP请求方法推断操作类型
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            String method = attributes.getRequest().getMethod();
            switch (method.toUpperCase()) {
                case "POST": return AuditLog.OperationType.CREATE;
                case "PUT": case "PATCH": return AuditLog.OperationType.UPDATE;
                case "DELETE": return AuditLog.OperationType.DELETE;
                case "GET": return AuditLog.OperationType.QUERY;
                default: return "UNKNOWN";
            }
        }
        
        return "UNKNOWN";
    }
    
    /**
     * 处理目标对象信息
     */
    private void processTargetInfo(AuditLog auditLog, ProceedingJoinPoint joinPoint, Auditable auditable) {
        // 设置目标ID
        String targetId = evaluateExpression(auditable.targetIdExpression(), joinPoint, null);
        if (targetId != null) {
            auditLog.setTargetId(targetId);
        }
        
        // 设置目标名称
        String targetName = evaluateExpression(auditable.targetNameExpression(), joinPoint, null);
        if (targetName != null) {
            auditLog.setTargetName(targetName);
        }
        
        // 如果没有指定targetType，尝试从方法参数推断
        if (!StringUtils.hasText(auditable.targetType())) {
            Object[] args = joinPoint.getArgs();
            if (args.length > 0 && args[0] != null) {
                String className = args[0].getClass().getSimpleName();
                auditLog.setTargetType(className);
            }
        }
    }
    
    /**
     * 设置HTTP请求信息
     */
    private void setRequestInfo(AuditLog auditLog, Auditable auditable) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            
            auditLog.setRequestMethod(request.getMethod());
            auditLog.setRequestUrl(request.getRequestURL().toString());
            
            // 记录请求参数
            if (auditable.includeParams()) {
                Map<String, String[]> parameterMap = request.getParameterMap();
                if (!parameterMap.isEmpty()) {
                    auditLog.setRequestParams(toJsonString(maskSensitiveData(parameterMap, auditable.sensitiveFields())));
                }
            }
            
            // 记录请求头
            if (auditable.includeHeaders()) {
                Map<String, String> headers = new HashMap<>();
                Enumeration<String> headerNames = request.getHeaderNames();
                while (headerNames.hasMoreElements()) {
                    String headerName = headerNames.nextElement();
                    headers.put(headerName, request.getHeader(headerName));
                }
                auditLog.setRequestHeaders(toJsonString(maskSensitiveData(headers, auditable.sensitiveFields())));
            }
            
            // 记录会话ID
            if (request.getSession(false) != null) {
                auditLog.setSessionId(request.getSession().getId());
            }
            
            // 记录用户代理
            auditLog.setUserAgent(request.getHeader("User-Agent"));
        }
    }
    
    /**
     * 设置用户和环境信息
     */
    private void setUserAndEnvironmentInfo(AuditLog auditLog) {
        // 从上下文获取当前用户信息
        try {
            Long currentUserId = AuditContextHolder.getCurrentUserId();
            String currentUsername = AuditContextHolder.getCurrentUsername();
            String currentUserRealName = AuditContextHolder.getCurrentUserRealName();
            
            auditLog.setUserId(currentUserId);
            auditLog.setUsername(currentUsername);
            auditLog.setUserRealName(currentUserRealName);
        } catch (Exception e) {
            log.debug("无法获取当前用户信息: {}", e.getMessage());
        }
        
        // 获取IP地址
        auditLog.setIpAddress(IpUtils.getClientIpAddress());
    }
    
    /**
     * 设置系统信息
     */
    private void setSystemInfo(AuditLog auditLog) {
        try {
            auditLog.setServerName(InetAddress.getLocalHost().getHostName());
        } catch (Exception e) {
            auditLog.setServerName("unknown");
        }
        
        auditLog.setThreadId(String.valueOf(Thread.currentThread().getId()));
        
        // 设置链路追踪ID（如果有的话）
        String traceId = AuditContextHolder.getTraceId();
        if (traceId != null) {
            auditLog.setTraceId(traceId);
        }
    }
    
    /**
     * 评估SpEL表达式
     */
    private String evaluateExpression(String expression, ProceedingJoinPoint joinPoint, String defaultValue) {
        if (!StringUtils.hasText(expression)) {
            return defaultValue;
        }
        
        try {
            Expression exp = parser.parseExpression(expression);
            EvaluationContext context = createEvaluationContext(joinPoint);
            Object result = exp.getValue(context);
            return result != null ? result.toString() : defaultValue;
        } catch (Exception e) {
            log.warn("SpEL表达式评估失败: {}", expression, e);
            return defaultValue;
        }
    }
    
    /**
     * 创建SpEL评估上下文
     */
    private EvaluationContext createEvaluationContext(ProceedingJoinPoint joinPoint) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        
        // 添加方法参数到上下文
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Parameter[] parameters = method.getParameters();
        Object[] args = joinPoint.getArgs();
        
        for (int i = 0; i < parameters.length && i < args.length; i++) {
            context.setVariable(parameters[i].getName(), args[i]);
        }
        
        return context;
    }
    
    /**
     * 对敏感数据进行脱敏处理
     */
    private Object maskSensitiveData(Object data, String[] sensitiveFields) {
        if (data == null || sensitiveFields.length == 0) {
            return data;
        }
        
        try {
            String json = toJsonString(data);
            for (String field : sensitiveFields) {
                // 简单的字符串替换，实际项目中可以使用更复杂的脱敏逻辑
                json = json.replaceAll("\"" + field + "\"\\s*:\\s*\"[^\"]*\"", "\"" + field + "\":\"***\"");
            }
            return json;
        } catch (Exception e) {
            log.warn("敏感数据脱敏失败", e);
            return data;
        }
    }
    
    /**
     * 将对象转换为JSON字符串
     */
    private String toJsonString(Object object) {
        if (object == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.warn("对象序列化为JSON失败", e);
            return object.toString();
        }
    }
}