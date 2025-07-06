package org.max.cms.leave.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 请假审批DTO
 */
@Data
public class LeaveApprovalDTO {
    
    @NotNull(message = "请假申请ID不能为空")
    private Long leaveApplicationId;
    
    @NotNull(message = "审批人ID不能为空")
    private Long approverId;
    
    @NotBlank(message = "审批动作不能为空")
    private String action; // approve, reject
    
    private String comment; // 审批意见
    
    private String taskId; // 工作流任务ID
}