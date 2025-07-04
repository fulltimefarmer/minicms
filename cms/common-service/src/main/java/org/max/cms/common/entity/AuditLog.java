package org.max.cms.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("audit_logs")
public class AuditLog extends BaseEntity {
    
    private String userId;
    private String username;
    private String action;
    private String resource;
    private String resourceId;
    private String method;
    private String requestPath;
    private String requestParams;
    private String requestBody;
    private String responseBody;
    private Integer statusCode;
    private String ipAddress;
    private String userAgent;
    private Long duration;
    private String errorMessage;
}