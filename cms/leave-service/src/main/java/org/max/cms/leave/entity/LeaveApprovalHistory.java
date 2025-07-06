package org.max.cms.leave.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.max.cms.common.entity.BaseEntity;
import org.max.cms.leave.enums.LeaveStatus;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 请假审批历史记录实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("leave_approval_history")
public class LeaveApprovalHistory extends BaseEntity {
    
    /**
     * 请假申请ID
     */
    @NotNull(message = "请假申请ID不能为空")
    private Long leaveApplicationId;
    
    /**
     * 审批人ID
     */
    @NotNull(message = "审批人ID不能为空")
    private Long approverId;
    
    /**
     * 审批人姓名
     */
    private String approverName;
    
    /**
     * 审批人部门ID
     */
    private Long approverDepartmentId;
    
    /**
     * 审批人部门名称
     */
    private String approverDepartmentName;
    
    /**
     * 审批前状态
     */
    private LeaveStatus beforeStatus;
    
    /**
     * 审批后状态
     */
    private LeaveStatus afterStatus;
    
    /**
     * 审批动作 (approve, reject, cancel)
     */
    private String action;
    
    /**
     * 审批意见
     */
    private String comment;
    
    /**
     * 审批时间
     */
    private LocalDateTime approvalTime;
    
    /**
     * 工作流任务ID
     */
    private String taskId;
    
    /**
     * 工作流任务名称
     */
    private String taskName;
    
    /**
     * 审批层级
     */
    private Integer approvalLevel;
}