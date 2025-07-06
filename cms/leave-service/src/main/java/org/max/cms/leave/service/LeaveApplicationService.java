package org.max.cms.leave.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.max.cms.leave.dto.LeaveApplicationDTO;
import org.max.cms.leave.dto.LeaveApprovalDTO;
import org.max.cms.leave.entity.LeaveApplication;
import org.max.cms.leave.entity.LeaveApprovalHistory;

import java.util.List;

/**
 * 请假申请服务接口
 */
public interface LeaveApplicationService {
    
    /**
     * 创建请假申请
     */
    LeaveApplication createLeaveApplication(LeaveApplicationDTO dto);
    
    /**
     * 更新请假申请
     */
    LeaveApplication updateLeaveApplication(Long id, LeaveApplicationDTO dto);
    
    /**
     * 删除请假申请
     */
    boolean deleteLeaveApplication(Long id);
    
    /**
     * 根据ID获取请假申请
     */
    LeaveApplication getLeaveApplicationById(Long id);
    
    /**
     * 分页查询请假申请
     */
    Page<LeaveApplication> getLeaveApplications(int page, int size, Long applicantId, String status);
    
    /**
     * 获取待审批的请假申请
     */
    Page<LeaveApplication> getPendingApprovals(int page, int size, Long approverId);
    
    /**
     * 审批请假申请
     */
    boolean approveLeaveApplication(LeaveApprovalDTO dto);
    
    /**
     * 取消请假申请
     */
    boolean cancelLeaveApplication(Long id, Long userId);
    
    /**
     * 获取请假申请的审批历史
     */
    List<LeaveApprovalHistory> getApprovalHistory(Long leaveApplicationId);
    
    /**
     * 启动工作流
     */
    String startWorkflow(LeaveApplication leaveApplication);
    
    /**
     * 完成工作流任务
     */
    void completeTask(String taskId, String action, String comment);
}