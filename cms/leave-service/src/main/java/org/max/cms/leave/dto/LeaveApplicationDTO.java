package org.max.cms.leave.dto;

import lombok.Data;
import org.max.cms.leave.enums.LeaveStatus;
import org.max.cms.leave.enums.LeaveType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 请假申请DTO
 */
@Data
public class LeaveApplicationDTO {
    
    private Long id;
    
    @NotNull(message = "申请人ID不能为空")
    private Long applicantId;
    
    private String applicantName;
    private Long departmentId;
    private String departmentName;
    
    @NotNull(message = "请假类型不能为空")
    private LeaveType leaveType;
    
    @NotNull(message = "请假开始日期不能为空")
    private LocalDate startDate;
    
    @NotNull(message = "请假结束日期不能为空")
    private LocalDate endDate;
    
    @NotNull(message = "请假天数不能为空")
    @Min(value = 1, message = "请假天数不能小于1")
    private BigDecimal days;
    
    @NotBlank(message = "请假原因不能为空")
    private String reason;
    
    private LeaveStatus status;
    private String currentApproverName;
    private String approvalRemark;
    private LocalDateTime finalApprovalTime;
    private String finalApproverName;
    private String attachmentPath;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}