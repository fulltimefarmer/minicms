package com.company.workflow;

import com.company.workflow.model.LeaveRequest;
import com.company.workflow.model.BusinessRequest;
import com.company.workflow.service.WorkflowService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class WorkflowServiceTest {

    @Autowired
    private WorkflowService workflowService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Test
    public void testLeaveRequestProcess() {
        // 创建请假申请
        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setEmployeeId("EMP001");
        leaveRequest.setEmployeeName("张三");
        leaveRequest.setLeaveType("年假");
        leaveRequest.setStartDate(LocalDate.now().plusDays(1));
        leaveRequest.setEndDate(LocalDate.now().plusDays(5));
        leaveRequest.setReason("休假");
        leaveRequest.setManagerId("MGR001");

        // 启动流程
        String processInstanceId = workflowService.startLeaveRequestProcess(leaveRequest);
        
        // 验证流程实例存在
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        assertNotNull(processInstance);
        
        // 验证有Manager审批任务
        List<Task> tasks = taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .taskDefinitionKey("ManagerApproval")
                .list();
        assertFalse(tasks.isEmpty());
        
        System.out.println("Leave request process started successfully with ID: " + processInstanceId);
    }

    @Test
    public void testBusinessRequestProcess() {
        // 创建业务申请
        BusinessRequest businessRequest = new BusinessRequest();
        businessRequest.setEmployeeId("EMP002");
        businessRequest.setEmployeeName("李四");
        businessRequest.setBusinessType("合同审批");
        businessRequest.setTitle("供应商合同");
        businessRequest.setDescription("新供应商合同审批");
        businessRequest.setManagerId("MGR002");

        // 启动流程
        String processInstanceId = workflowService.startBusinessRequestProcess(businessRequest);
        
        // 验证流程实例存在
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        assertNotNull(processInstance);
        
        // 验证有文档审查任务
        List<Task> tasks = taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .taskDefinitionKey("DocumentReview")
                .list();
        assertFalse(tasks.isEmpty());
        
        System.out.println("Business request process started successfully with ID: " + processInstanceId);
    }

    @Test
    public void testTaskManagement() {
        // 创建一个测试请假申请
        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setEmployeeId("EMP003");
        leaveRequest.setEmployeeName("王五");
        leaveRequest.setLeaveType("病假");
        leaveRequest.setStartDate(LocalDate.now().plusDays(1));
        leaveRequest.setEndDate(LocalDate.now().plusDays(3));
        leaveRequest.setReason("身体不适");
        leaveRequest.setManagerId("MGR003");

        // 启动流程
        String processInstanceId = workflowService.startLeaveRequestProcess(leaveRequest);
        
        // 获取任务
        List<Task> tasks = taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .list();
        
        assertFalse(tasks.isEmpty());
        
        Task task = tasks.get(0);
        
        // 测试认领任务
        workflowService.claimTask(task.getId(), "MGR003");
        
        // 验证任务已被认领
        Task claimedTask = taskService.createTaskQuery()
                .taskId(task.getId())
                .singleResult();
        assertEquals("MGR003", claimedTask.getAssignee());
        
        System.out.println("Task management test completed successfully");
    }
}