package org.max.cms.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 审计日志实体类
 * 用于记录系统中所有用户操作和行为
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("audit_logs")
public class AuditLog {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    // ==================== 操作基本信息 ====================
    /**
     * 操作类型: CREATE, UPDATE, DELETE, LOGIN, LOGOUT, QUERY等
     */
    @TableField("operation_type")
    private String operationType;
    
    /**
     * 操作名称: 用户登录、创建用户、修改资产等
     */
    @TableField("operation_name")
    private String operationName;
    
    /**
     * 操作描述
     */
    @TableField("operation_desc")
    private String operationDesc;
    
    // ==================== 请求信息 ====================
    /**
     * HTTP方法: GET, POST, PUT, DELETE
     */
    @TableField("request_method")
    private String requestMethod;
    
    /**
     * 请求URL
     */
    @TableField("request_url")
    private String requestUrl;
    
    /**
     * 请求参数(JSON格式)
     */
    @TableField("request_params")
    private String requestParams;
    
    /**
     * 请求体(JSON格式)
     */
    @TableField("request_body")
    private String requestBody;
    
    /**
     * 请求头(JSON格式)
     */
    @TableField("request_headers")
    private String requestHeaders;
    
    // ==================== 响应信息 ====================
    /**
     * HTTP响应状态码
     */
    @TableField("response_status")
    private Integer responseStatus;
    
    /**
     * 响应体(JSON格式)
     */
    @TableField("response_body")
    private String responseBody;
    
    /**
     * 响应时间(毫秒)
     */
    @TableField("response_time")
    private Long responseTime;
    
    // ==================== 业务信息 ====================
    /**
     * 业务模块: USER, ASSET, AUTH, SYSTEM等
     */
    @TableField("business_module")
    private String businessModule;
    
    /**
     * 目标对象类型: User, Asset, Role等
     */
    @TableField("target_type")
    private String targetType;
    
    /**
     * 目标对象ID
     */
    @TableField("target_id")
    private String targetId;
    
    /**
     * 目标对象名称
     */
    @TableField("target_name")
    private String targetName;
    
    // ==================== 变更信息 ====================
    /**
     * 修改前的值(JSON格式)
     */
    @TableField("old_values")
    private String oldValues;
    
    /**
     * 修改后的值(JSON格式)
     */
    @TableField("new_values")
    private String newValues;
    
    /**
     * 变更字段列表(JSON数组)
     */
    @TableField("changed_fields")
    private String changedFields;
    
    // ==================== 用户和环境信息 ====================
    /**
     * 操作用户ID
     */
    @TableField("user_id")
    private Long userId;
    
    /**
     * 操作用户名
     */
    @TableField("username")
    private String username;
    
    /**
     * 操作用户真实姓名
     */
    @TableField("user_real_name")
    private String userRealName;
    
    /**
     * 客户端IP地址
     */
    @TableField("ip_address")
    private String ipAddress;
    
    /**
     * 用户代理信息
     */
    @TableField("user_agent")
    private String userAgent;
    
    /**
     * 会话ID
     */
    @TableField("session_id")
    private String sessionId;
    
    // ==================== 系统信息 ====================
    /**
     * 服务器名称
     */
    @TableField("server_name")
    private String serverName;
    
    /**
     * 线程ID
     */
    @TableField("thread_id")
    private String threadId;
    
    /**
     * 链路追踪ID
     */
    @TableField("trace_id")
    private String traceId;
    
    // ==================== 状态和结果 ====================
    /**
     * 操作状态: SUCCESS, FAILED, PARTIAL
     */
    @TableField("status")
    private String status;
    
    /**
     * 错误信息
     */
    @TableField("error_message")
    private String errorMessage;
    
    /**
     * 风险级别: LOW, MEDIUM, HIGH, CRITICAL
     */
    @TableField("risk_level")
    private String riskLevel;
    
    // ==================== 时间信息 ====================
    /**
     * 操作开始时间
     */
    @TableField("start_time")
    private LocalDateTime startTime;
    
    /**
     * 操作结束时间
     */
    @TableField("end_time")
    private LocalDateTime endTime;
    
    /**
     * 操作日期(用于分区查询)
     */
    @TableField("operation_date")
    private LocalDate operationDate;
    
    // ==================== 审计元数据 ====================
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(value = "created_by", fill = FieldFill.INSERT)
    private String createdBy;
    
    @TableField("deleted")
    @TableLogic
    private Boolean deleted;
    
    // ==================== 枚举常量 ====================
    
    /**
     * 操作类型枚举
     */
    public static class OperationType {
        public static final String CREATE = "CREATE";
        public static final String UPDATE = "UPDATE";
        public static final String DELETE = "DELETE";
        public static final String QUERY = "QUERY";
        public static final String LOGIN = "LOGIN";
        public static final String LOGOUT = "LOGOUT";
        public static final String EXPORT = "EXPORT";
        public static final String IMPORT = "IMPORT";
        public static final String UPLOAD = "UPLOAD";
        public static final String DOWNLOAD = "DOWNLOAD";
    }
    
    /**
     * 业务模块枚举
     */
    public static class BusinessModule {
        public static final String USER = "USER";
        public static final String AUTH = "AUTH";
        public static final String ASSET = "ASSET";
        public static final String ROLE = "ROLE";
        public static final String PERMISSION = "PERMISSION";
        public static final String SYSTEM = "SYSTEM";
        public static final String DEPARTMENT = "DEPARTMENT";
        public static final String DOCUMENT = "DOCUMENT";
    }
    
    /**
     * 操作状态枚举
     */
    public static class Status {
        public static final String SUCCESS = "SUCCESS";
        public static final String FAILED = "FAILED";
        public static final String PARTIAL = "PARTIAL";
    }
    
    /**
     * 风险级别枚举
     */
    public static class RiskLevel {
        public static final String LOW = "LOW";
        public static final String MEDIUM = "MEDIUM";
        public static final String HIGH = "HIGH";
        public static final String CRITICAL = "CRITICAL";
    }
}