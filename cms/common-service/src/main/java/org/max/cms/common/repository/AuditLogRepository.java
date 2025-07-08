package org.max.cms.common.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.max.cms.common.entity.AuditLog;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 审计日志数据访问层
 */
@Mapper
public interface AuditLogRepository extends BaseMapper<AuditLog> {
    
    /**
     * 分页查询审计日志
     */
    @Select({
        "<script>",
        "SELECT * FROM audit_logs",
        "WHERE deleted = false",
        "<if test='query.userId != null'>",
        "  AND user_id = #{query.userId}",
        "</if>",
        "<if test='query.username != null and query.username != &quot;&quot;'>",
        "  AND username LIKE CONCAT('%', #{query.username}, '%')",
        "</if>",
        "<if test='query.operationType != null and query.operationType != &quot;&quot;'>",
        "  AND operation_type = #{query.operationType}",
        "</if>",
        "<if test='query.businessModule != null and query.businessModule != &quot;&quot;'>",
        "  AND business_module = #{query.businessModule}",
        "</if>",
        "<if test='query.status != null and query.status != &quot;&quot;'>",
        "  AND status = #{query.status}",
        "</if>",
        "<if test='query.riskLevel != null and query.riskLevel != &quot;&quot;'>",
        "  AND risk_level = #{query.riskLevel}",
        "</if>",
        "<if test='query.ipAddress != null and query.ipAddress != &quot;&quot;'>",
        "  AND ip_address = #{query.ipAddress}",
        "</if>",
        "<if test='query.startTime != null'>",
        "  AND start_time >= #{query.startTime}",
        "</if>",
        "<if test='query.endTime != null'>",
        "  AND start_time <= #{query.endTime}",
        "</if>",
        "<if test='query.keyword != null and query.keyword != &quot;&quot;'>",
        "  AND (",
        "    operation_name LIKE CONCAT('%', #{query.keyword}, '%')",
        "    OR operation_desc LIKE CONCAT('%', #{query.keyword}, '%')",
        "    OR target_name LIKE CONCAT('%', #{query.keyword}, '%')",
        "  )",
        "</if>",
        "ORDER BY start_time DESC",
        "</script>"
    })
    IPage<AuditLog> selectAuditLogPage(Page<AuditLog> page, @Param("query") AuditLogQuery query);
    
    /**
     * 根据用户ID查询最近的登录日志
     */
    @Select({
        "SELECT * FROM audit_logs",
        "WHERE user_id = #{userId}",
        "  AND operation_type = 'LOGIN'",
        "  AND status = 'SUCCESS'",
        "  AND deleted = false",
        "ORDER BY start_time DESC",
        "LIMIT #{limit}"
    })
    List<AuditLog> selectRecentLoginLogs(@Param("userId") Long userId, @Param("limit") int limit);
    
    /**
     * 统计操作类型分布
     */
    @Select({
        "SELECT operation_type, COUNT(*) as count",
        "FROM audit_logs",
        "WHERE operation_date >= #{startDate}",
        "  AND operation_date <= #{endDate}",
        "  AND deleted = false",
        "GROUP BY operation_type",
        "ORDER BY count DESC"
    })
    List<Map<String, Object>> countByOperationType(@Param("startDate") LocalDate startDate, 
                                                   @Param("endDate") LocalDate endDate);
    
    /**
     * 统计风险级别分布
     */
    @Select({
        "SELECT risk_level, COUNT(*) as count",
        "FROM audit_logs",
        "WHERE operation_date >= #{startDate}",
        "  AND operation_date <= #{endDate}",
        "  AND deleted = false",
        "GROUP BY risk_level",
        "ORDER BY count DESC"
    })
    List<Map<String, Object>> countByRiskLevel(@Param("startDate") LocalDate startDate, 
                                               @Param("endDate") LocalDate endDate);
    
    /**
     * 统计每日操作数量
     */
    @Select({
        "SELECT operation_date, COUNT(*) as count",
        "FROM audit_logs",
        "WHERE operation_date >= #{startDate}",
        "  AND operation_date <= #{endDate}",
        "  AND deleted = false",
        "GROUP BY operation_date",
        "ORDER BY operation_date ASC"
    })
    List<Map<String, Object>> countByDate(@Param("startDate") LocalDate startDate, 
                                          @Param("endDate") LocalDate endDate);
    
    /**
     * 查询异常操作日志
     */
    @Select({
        "SELECT * FROM audit_logs",
        "WHERE (",
        "  status = 'FAILED'",
        "  OR risk_level IN ('HIGH', 'CRITICAL')",
        "  OR response_time > #{slowThreshold}",
        ")",
        "AND operation_date >= #{startDate}",
        "AND deleted = false",
        "ORDER BY start_time DESC",
        "LIMIT #{limit}"
    })
    List<AuditLog> selectAbnormalLogs(@Param("startDate") LocalDate startDate, 
                                      @Param("slowThreshold") long slowThreshold,
                                      @Param("limit") int limit);
    
    /**
     * 查询用户的操作统计
     */
    @Select({
        "SELECT",
        "  user_id,",
        "  username,",
        "  COUNT(*) as total_operations,",
        "  COUNT(CASE WHEN status = 'SUCCESS' THEN 1 END) as success_count,",
        "  COUNT(CASE WHEN status = 'FAILED' THEN 1 END) as failed_count,",
        "  COUNT(CASE WHEN risk_level = 'HIGH' THEN 1 END) as high_risk_count,",
        "  COUNT(CASE WHEN risk_level = 'CRITICAL' THEN 1 END) as critical_risk_count",
        "FROM audit_logs",
        "WHERE operation_date >= #{startDate}",
        "  AND operation_date <= #{endDate}",
        "  AND user_id IS NOT NULL",
        "  AND deleted = false",
        "GROUP BY user_id, username",
        "ORDER BY total_operations DESC",
        "LIMIT #{limit}"
    })
    List<Map<String, Object>> selectUserOperationStats(@Param("startDate") LocalDate startDate,
                                                        @Param("endDate") LocalDate endDate,
                                                        @Param("limit") int limit);
    
    /**
     * 清理过期的审计日志
     */
    @Delete({
        "DELETE FROM audit_logs",
        "WHERE operation_date < #{expireDate}"
    })
    int deleteExpiredLogs(@Param("expireDate") LocalDate expireDate);
    
    /**
     * 审计日志查询条件类
     */
    class AuditLogQuery {
        private Long userId;
        private String username;
        private String operationType;
        private String businessModule;
        private String status;
        private String riskLevel;
        private String ipAddress;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private String keyword;
        
        // Getters and Setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getOperationType() { return operationType; }
        public void setOperationType(String operationType) { this.operationType = operationType; }
        
        public String getBusinessModule() { return businessModule; }
        public void setBusinessModule(String businessModule) { this.businessModule = businessModule; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public String getRiskLevel() { return riskLevel; }
        public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
        
        public String getIpAddress() { return ipAddress; }
        public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
        
        public LocalDateTime getStartTime() { return startTime; }
        public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
        
        public LocalDateTime getEndTime() { return endTime; }
        public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
        
        public String getKeyword() { return keyword; }
        public void setKeyword(String keyword) { this.keyword = keyword; }
    }
}