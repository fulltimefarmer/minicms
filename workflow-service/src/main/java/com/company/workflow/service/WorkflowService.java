package com.company.workflow.service;

import com.company.workflow.model.BusinessRequest;
import com.company.workflow.model.LeaveRequest;
import com.company.workflow.repository.BusinessRequestRepository;
import com.company.workflow.repository.LeaveRequestRepository;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class WorkflowService {
    
    @Autowired
    private RuntimeService runtimeService;
    
    @Autowired
    private TaskService taskService;
    
    @Autowired
    private LeaveRequestRepository leaveRequestRepository;
    
    @Autowired
    private BusinessRequestRepository businessRequestRepository;
    
    // 启动员工请假流程
    public String startLeaveRequestProcess(LeaveRequest leaveRequest) {
        // 保存请假申请
        leaveRequest = leaveRequestRepository.save(leaveRequest);
        
        // 准备流程变量
        Map<String, Object> variables = new HashMap<>();
        variables.put("leaveRequestId", leaveRequest.getId());
        variables.put("employeeId", leaveRequest.getEmployeeId());
        variables.put("employeeName", leaveRequest.getEmployeeName());
        variables.put("leaveType", leaveRequest.getLeaveType());
        variables.put("startDate", leaveRequest.getStartDate());
        variables.put("endDate", leaveRequest.getEndDate());
        variables.put("reason", leaveRequest.getReason());
        variables.put("managerId", leaveRequest.getManagerId());
        
        // 启动流程实例
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
                "leave-request-process", variables);
        
        // 更新请假申请的流程实例ID
        leaveRequest.setProcessInstanceId(processInstance.getId());
        leaveRequestRepository.save(leaveRequest);
        
        return processInstance.getId();
    }
    
    // 启动业务流程
    public String startBusinessRequestProcess(BusinessRequest businessRequest) {
        // 保存业务申请
        businessRequest = businessRequestRepository.save(businessRequest);
        
        // 准备流程变量
        Map<String, Object> variables = new HashMap<>();
        variables.put("businessRequestId", businessRequest.getId());
        variables.put("employeeId", businessRequest.getEmployeeId());
        variables.put("employeeName", businessRequest.getEmployeeName());
        variables.put("businessType", businessRequest.getBusinessType());
        variables.put("title", businessRequest.getTitle());
        variables.put("description", businessRequest.getDescription());
        variables.put("contractFileName", businessRequest.getContractFileName());
        variables.put("contractFilePath", businessRequest.getContractFilePath());
        variables.put("managerId", businessRequest.getManagerId());
        
        // 启动流程实例
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
                "business-request-process", variables);
        
        // 更新业务申请的流程实例ID
        businessRequest.setProcessInstanceId(processInstance.getId());
        businessRequestRepository.save(businessRequest);
        
        return processInstance.getId();
    }
    
    // 完成任务
    public void completeTask(String taskId, Map<String, Object> variables) {
        taskService.complete(taskId, variables);
    }
    
    // 获取用户任务列表
    public List<Task> getUserTasks(String userId) {
        return taskService.createTaskQuery()
                .taskAssignee(userId)
                .active()
                .orderByTaskCreateTime()
                .desc()
                .list();
    }
    
    // 获取用户候选任务列表
    public List<Task> getUserCandidateTasks(String userId) {
        return taskService.createTaskQuery()
                .taskCandidateUser(userId)
                .active()
                .orderByTaskCreateTime()
                .desc()
                .list();
    }
    
    // 获取组候选任务列表
    public List<Task> getGroupCandidateTasks(String groupId) {
        return taskService.createTaskQuery()
                .taskCandidateGroup(groupId)
                .active()
                .orderByTaskCreateTime()
                .desc()
                .list();
    }
    
    // 认领任务
    public void claimTask(String taskId, String userId) {
        taskService.claim(taskId, userId);
    }
    
    // 审批请假申请
    public void approveLeaveRequest(String taskId, String managerId, String decision, String comment) {
        // 获取任务变量
        Map<String, Object> variables = taskService.getVariables(taskId);
        Long leaveRequestId = (Long) variables.get("leaveRequestId");
        
        // 更新请假申请状态
        Optional<LeaveRequest> leaveRequestOpt = leaveRequestRepository.findById(leaveRequestId);
        if (leaveRequestOpt.isPresent()) {
            LeaveRequest leaveRequest = leaveRequestOpt.get();
            leaveRequest.setStatus(decision);
            leaveRequest.setManagerId(managerId);
            leaveRequest.setManagerComment(comment);
            leaveRequestRepository.save(leaveRequest);
        }
        
        // 设置流程变量
        Map<String, Object> taskVariables = new HashMap<>();
        taskVariables.put("approved", "APPROVED".equals(decision));
        taskVariables.put("managerComment", comment);
        taskVariables.put("managerId", managerId);
        
        // 完成任务
        taskService.complete(taskId, taskVariables);
    }
    
    // 审批业务申请
    public void approveBusinessRequest(String taskId, String managerId, String decision, String comment) {
        // 获取任务变量
        Map<String, Object> variables = taskService.getVariables(taskId);
        Long businessRequestId = (Long) variables.get("businessRequestId");
        
        // 更新业务申请状态
        Optional<BusinessRequest> businessRequestOpt = businessRequestRepository.findById(businessRequestId);
        if (businessRequestOpt.isPresent()) {
            BusinessRequest businessRequest = businessRequestOpt.get();
            businessRequest.setStatus(decision);
            businessRequest.setManagerId(managerId);
            businessRequest.setManagerComment(comment);
            businessRequestRepository.save(businessRequest);
        }
        
        // 设置流程变量
        Map<String, Object> taskVariables = new HashMap<>();
        taskVariables.put("approved", "APPROVED".equals(decision));
        taskVariables.put("managerComment", comment);
        taskVariables.put("managerId", managerId);
        
        // 完成任务
        taskService.complete(taskId, taskVariables);
    }
    
    // 获取任务详情
    public Task getTaskById(String taskId) {
        return taskService.createTaskQuery().taskId(taskId).singleResult();
    }
    
    // 获取任务变量
    public Map<String, Object> getTaskVariables(String taskId) {
        return taskService.getVariables(taskId);
    }
}