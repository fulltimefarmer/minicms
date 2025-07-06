package org.max.cms.leave.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.max.cms.common.entity.BaseEntity;
import org.max.cms.leave.enums.LeaveStatus;
import org.max.cms.leave.enums.LeaveType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 请假申请实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("leave_applications")
public class LeaveApplication extends BaseEntity {
    
    /**
     * 申请人ID
     */
    @NotNull(message = "申请人ID不能为空")
    private Long applicantId;
    
    /**
     * 申请人姓名
     */
    private String applicantName;
    
    /**
     * 申请人部门ID
     */
    private Long departmentId;
    
    /**
     * 申请人部门名称
     */
    private String departmentName;
    
    /**
     * 请假类型
     */
    @NotNull(message = "请假类型不能为空")
    private LeaveType leaveType;
    
    /**
     * 请假开始日期
     */
    @NotNull(message = "请假开始日期不能为空")
    private LocalDate startDate;
    
    /**
     * 请假结束日期
     */
    @NotNull(message = "请假结束日期不能为空")
    private LocalDate endDate;
    
    /**
     * 请假天数
     */
    @NotNull(message = "请假天数不能为空")
    @Min(value = 1, message = "请假天数不能小于1")
    private BigDecimal days;
    
    /**
     * 请假原因
     */
    @NotBlank(message = "请假原因不能为空")
    private String reason;
    
    /**
     * 请假状态
     */
    @Builder.Default
    private LeaveStatus status = LeaveStatus.PENDING;
    
    /**
     * 当前审批人ID
     */
    private Long currentApproverId;
    
    /**
     * 当前审批人姓名
     */
    private String currentApproverName;
    
    /**
     * 工作流程实例ID
     */
    private String processInstanceId;
    
    /**
     * 工作流程定义ID
     */
    private String processDefinitionId;
    
    /**
     * 当前任务ID
     */
    private String taskId;
    
    /**
     * 审批备注
     */
    private String approvalRemark;
    
    /**
     * 最终审批时间
     */
    private LocalDateTime finalApprovalTime;
    
    /**
     * 最终审批人ID
     */
    private Long finalApproverId;
    
    /**
     * 最终审批人姓名
     */
    private String finalApproverName;
    
    /**
     * 附件路径
     */
    private String attachmentPath;
}