package org.max.cms.common.util;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;

/**
 * 审计上下文持有者
 * 用于在整个请求过程中维护当前用户信息和链路追踪信息
 */
public class AuditContextHolder {
    
    private static final ThreadLocal<AuditContext> CONTEXT_HOLDER = new ThreadLocal<>();
    
    /**
     * 设置当前审计上下文
     */
    public static void setContext(AuditContext context) {
        CONTEXT_HOLDER.set(context);
    }
    
    /**
     * 获取当前审计上下文
     */
    public static AuditContext getContext() {
        AuditContext context = CONTEXT_HOLDER.get();
        if (context == null) {
            context = createDefaultContext();
            CONTEXT_HOLDER.set(context);
        }
        return context;
    }
    
    /**
     * 清除当前审计上下文
     */
    public static void clearContext() {
        CONTEXT_HOLDER.remove();
    }
    
    /**
     * 获取当前用户ID
     */
    public static Long getCurrentUserId() {
        return getContext().getUserId();
    }
    
    /**
     * 获取当前用户名
     */
    public static String getCurrentUsername() {
        return getContext().getUsername();
    }
    
    /**
     * 获取当前用户真实姓名
     */
    public static String getCurrentUserRealName() {
        return getContext().getUserRealName();
    }
    
    /**
     * 获取链路追踪ID
     */
    public static String getTraceId() {
        return getContext().getTraceId();
    }
    
    /**
     * 设置当前用户信息
     */
    public static void setCurrentUser(Long userId, String username, String userRealName) {
        AuditContext context = getContext();
        context.setUserId(userId);
        context.setUsername(username);
        context.setUserRealName(userRealName);
    }
    
    /**
     * 设置链路追踪ID
     */
    public static void setTraceId(String traceId) {
        getContext().setTraceId(traceId);
    }
    
    /**
     * 创建默认上下文
     */
    private static AuditContext createDefaultContext() {
        AuditContext context = new AuditContext();
        
        // 尝试从请求头获取链路追踪ID
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String traceId = request.getHeader("X-Trace-Id");
                if (traceId == null) {
                    traceId = UUID.randomUUID().toString().replace("-", "");
                }
                context.setTraceId(traceId);
            }
        } catch (Exception e) {
            // 生成新的追踪ID
            context.setTraceId(UUID.randomUUID().toString().replace("-", ""));
        }
        
        return context;
    }
    
    /**
     * 审计上下文数据类
     */
    public static class AuditContext {
        private Long userId;
        private String username;
        private String userRealName;
        private String traceId;
        
        public Long getUserId() {
            return userId;
        }
        
        public void setUserId(Long userId) {
            this.userId = userId;
        }
        
        public String getUsername() {
            return username;
        }
        
        public void setUsername(String username) {
            this.username = username;
        }
        
        public String getUserRealName() {
            return userRealName;
        }
        
        public void setUserRealName(String userRealName) {
            this.userRealName = userRealName;
        }
        
        public String getTraceId() {
            return traceId;
        }
        
        public void setTraceId(String traceId) {
            this.traceId = traceId;
        }
    }
}