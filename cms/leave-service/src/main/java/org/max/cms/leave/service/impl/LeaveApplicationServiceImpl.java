package org.max.cms.leave.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.max.cms.leave.dto.LeaveApplicationDTO;
import org.max.cms.leave.dto.LeaveApprovalDTO;
import org.max.cms.leave.entity.LeaveApplication;
import org.max.cms.leave.entity.LeaveApprovalHistory;
import org.max.cms.leave.enums.LeaveStatus;
import org.max.cms.leave.mapper.LeaveApplicationMapper;
import org.max.cms.leave.mapper.LeaveApprovalHistoryMapper;
import org.max.cms.leave.service.LeaveApplicationService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 请假申请服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LeaveApplicationServiceImpl implements LeaveApplicationService {
    
    private final LeaveApplicationMapper leaveApplicationMapper;
    private final LeaveApprovalHistoryMapper approvalHistoryMapper;
    private final RuntimeService runtimeService;
    private final TaskService taskService;
    
    @Override
    @Transactional
    public LeaveApplication createLeaveApplication(LeaveApplicationDTO dto) {
        // 创建请假申请实体
        LeaveApplication leaveApplication = new LeaveApplication();
        BeanUtils.copyProperties(dto, leaveApplication);
        
        // 设置默认状态
        leaveApplication.setStatus(LeaveStatus.PENDING);
        
        // 保存到数据库
        leaveApplicationMapper.insert(leaveApplication);
        
        // 启动工作流程
        String processInstanceId = startWorkflow(leaveApplication);
        leaveApplication.setProcessInstanceId(processInstanceId);
        
        // 更新工作流程实例ID
        leaveApplicationMapper.updateById(leaveApplication);
        
        log.info("创建请假申请成功, ID: {}, 工作流实例ID: {}", leaveApplication.getId(), processInstanceId);
        return leaveApplication;
    }
    
    @Override
    @Transactional
    public LeaveApplication updateLeaveApplication(Long id, LeaveApplicationDTO dto) {
        LeaveApplication existingApplication = leaveApplicationMapper.selectById(id);
        if (existingApplication == null) {
            throw new RuntimeException("请假申请不存在");
        }
        
        // 只有待审核状态才能修改
        if (existingApplication.getStatus() != LeaveStatus.PENDING) {
            throw new RuntimeException("只有待审核状态的申请才能修改");
        }
        
        // 更新属性
        BeanUtils.copyProperties(dto, existingApplication, "id", "status", "processInstanceId");
        
        leaveApplicationMapper.updateById(existingApplication);
        
        log.info("更新请假申请成功, ID: {}", id);
        return existingApplication;
    }
    
    @Override
    @Transactional
    public boolean deleteLeaveApplication(Long id) {
        LeaveApplication application = leaveApplicationMapper.selectById(id);
        if (application == null) {
            return false;
        }
        
        // 只有待审核状态才能删除
        if (application.getStatus() != LeaveStatus.PENDING) {
            throw new RuntimeException("只有待审核状态的申请才能删除");
        }
        
        // 删除工作流程实例
        if (StringUtils.isNotBlank(application.getProcessInstanceId())) {
            try {
                runtimeService.deleteProcessInstance(application.getProcessInstanceId(), "用户删除");
            } catch (Exception e) {
                log.warn("删除工作流程实例失败: {}", e.getMessage());
            }
        }
        
        // 删除申请记录
        leaveApplicationMapper.deleteById(id);
        
        log.info("删除请假申请成功, ID: {}", id);
        return true;
    }
    
    @Override
    public LeaveApplication getLeaveApplicationById(Long id) {
        return leaveApplicationMapper.selectById(id);
    }
    
    @Override
    public Page<LeaveApplication> getLeaveApplications(int page, int size, Long applicantId, String status) {
        Page<LeaveApplication> pageParam = new Page<>(page, size);
        QueryWrapper<LeaveApplication> wrapper = new QueryWrapper<>();
        
        if (applicantId != null) {
            wrapper.eq("applicant_id", applicantId);
        }
        
        if (StringUtils.isNotBlank(status)) {
            wrapper.eq("status", status);
        }
        
        wrapper.orderByDesc("created_at");
        
        return leaveApplicationMapper.selectPage(pageParam, wrapper);
    }
    
    @Override
    public Page<LeaveApplication> getPendingApprovals(int page, int size, Long approverId) {
        Page<LeaveApplication> pageParam = new Page<>(page, size);
        QueryWrapper<LeaveApplication> wrapper = new QueryWrapper<>();
        
        wrapper.eq("current_approver_id", approverId)
               .eq("status", LeaveStatus.PENDING)
               .orderByDesc("created_at");
        
        return leaveApplicationMapper.selectPage(pageParam, wrapper);
    }
    
    @Override
    @Transactional
    public boolean approveLeaveApplication(LeaveApprovalDTO dto) {
        LeaveApplication application = leaveApplicationMapper.selectById(dto.getLeaveApplicationId());
        if (application == null) {
            throw new RuntimeException("请假申请不存在");
        }
        
        // 记录审批历史
        LeaveApprovalHistory history = LeaveApprovalHistory.builder()
                .leaveApplicationId(dto.getLeaveApplicationId())
                .approverId(dto.getApproverId())
                .beforeStatus(application.getStatus())
                .action(dto.getAction())
                .comment(dto.getComment())
                .approvalTime(LocalDateTime.now())
                .taskId(dto.getTaskId())
                .build();
        
        // 完成工作流任务
        completeTask(dto.getTaskId(), dto.getAction(), dto.getComment());
        
        // 更新申请状态
        LeaveStatus newStatus = "approve".equals(dto.getAction()) ? LeaveStatus.APPROVED : LeaveStatus.REJECTED;
        application.setStatus(newStatus);
        application.setApprovalRemark(dto.getComment());
        application.setFinalApprovalTime(LocalDateTime.now());
        application.setFinalApproverId(dto.getApproverId());
        
        history.setAfterStatus(newStatus);
        
        // 保存
        leaveApplicationMapper.updateById(application);
        approvalHistoryMapper.insert(history);
        
        log.info("审批请假申请成功, ID: {}, 动作: {}", dto.getLeaveApplicationId(), dto.getAction());
        return true;
    }
    
    @Override
    @Transactional
    public boolean cancelLeaveApplication(Long id, Long userId) {
        LeaveApplication application = leaveApplicationMapper.selectById(id);
        if (application == null) {
            return false;
        }
        
        // 只有申请人才能取消
        if (!application.getApplicantId().equals(userId)) {
            throw new RuntimeException("只有申请人才能取消申请");
        }
        
        // 只有待审核状态才能取消
        if (application.getStatus() != LeaveStatus.PENDING) {
            throw new RuntimeException("只有待审核状态的申请才能取消");
        }
        
        // 记录取消历史
        LeaveApprovalHistory history = LeaveApprovalHistory.builder()
                .leaveApplicationId(id)
                .approverId(userId)
                .beforeStatus(application.getStatus())
                .afterStatus(LeaveStatus.CANCELLED)
                .action("cancel")
                .comment("申请人取消")
                .approvalTime(LocalDateTime.now())
                .build();
        
        // 更新状态
        application.setStatus(LeaveStatus.CANCELLED);
        
        // 删除工作流程实例
        if (StringUtils.isNotBlank(application.getProcessInstanceId())) {
            try {
                runtimeService.deleteProcessInstance(application.getProcessInstanceId(), "申请人取消");
            } catch (Exception e) {
                log.warn("删除工作流程实例失败: {}", e.getMessage());
            }
        }
        
        leaveApplicationMapper.updateById(application);
        approvalHistoryMapper.insert(history);
        
        log.info("取消请假申请成功, ID: {}", id);
        return true;
    }
    
    @Override
    public List<LeaveApprovalHistory> getApprovalHistory(Long leaveApplicationId) {
        QueryWrapper<LeaveApprovalHistory> wrapper = new QueryWrapper<>();
        wrapper.eq("leave_application_id", leaveApplicationId)
               .orderByAsc("created_at");
        return approvalHistoryMapper.selectList(wrapper);
    }
    
    @Override
    public String startWorkflow(LeaveApplication leaveApplication) {
        // 设置流程变量
        Map<String, Object> variables = new HashMap<>();
        variables.put("applicantId", leaveApplication.getApplicantId());
        variables.put("leaveApplicationId", leaveApplication.getId());
        variables.put("leaveType", leaveApplication.getLeaveType().name());
        variables.put("days", leaveApplication.getDays());
        variables.put("departmentId", leaveApplication.getDepartmentId());
        
        // 启动流程
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
                "leaveProcess", 
                leaveApplication.getId().toString(), 
                variables
        );
        
        // 查找第一个任务
        Task firstTask = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .singleResult();
        
        if (firstTask != null) {
            // 更新当前任务信息
            leaveApplication.setTaskId(firstTask.getId());
            leaveApplication.setCurrentApproverId(Long.valueOf(firstTask.getAssignee()));
        }
        
        return processInstance.getId();
    }
    
    @Override
    public void completeTask(String taskId, String action, String comment) {
        if (StringUtils.isBlank(taskId)) {
            throw new RuntimeException("任务ID不能为空");
        }
        
        Map<String, Object> variables = new HashMap<>();
        variables.put("approved", "approve".equals(action));
        variables.put("comment", comment);
        
        taskService.complete(taskId, variables);
    }
}