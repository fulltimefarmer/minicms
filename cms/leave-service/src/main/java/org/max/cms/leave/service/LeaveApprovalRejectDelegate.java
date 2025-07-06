package org.max.cms.leave.service;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.max.cms.leave.entity.LeaveApplication;
import org.max.cms.leave.enums.LeaveStatus;
import org.max.cms.leave.mapper.LeaveApplicationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 审批拒绝处理委托
 */
@Component
public class LeaveApprovalRejectDelegate implements JavaDelegate {
    
    @Autowired
    private LeaveApplicationMapper leaveApplicationMapper;
    
    @Override
    public void execute(DelegateExecution execution) {
        Long leaveApplicationId = (Long) execution.getVariable("leaveApplicationId");
        String comment = (String) execution.getVariable("comment");
        
        // 更新请假申请状态
        LeaveApplication application = leaveApplicationMapper.selectById(leaveApplicationId);
        if (application != null) {
            application.setStatus(LeaveStatus.REJECTED);
            application.setApprovalRemark(comment);
            application.setFinalApprovalTime(LocalDateTime.now());
            leaveApplicationMapper.updateById(application);
        }
    }
}