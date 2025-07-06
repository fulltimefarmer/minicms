package com.company.workflow.controller;

import com.company.workflow.model.BusinessRequest;
import com.company.workflow.model.LeaveRequest;
import com.company.workflow.repository.BusinessRequestRepository;
import com.company.workflow.repository.LeaveRequestRepository;
import com.company.workflow.service.FileUploadService;
import com.company.workflow.service.WorkflowService;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/workflow")
@CrossOrigin(origins = "*")
public class WorkflowController {
    
    @Autowired
    private WorkflowService workflowService;
    
    @Autowired
    private FileUploadService fileUploadService;
    
    @Autowired
    private LeaveRequestRepository leaveRequestRepository;
    
    @Autowired
    private BusinessRequestRepository businessRequestRepository;
    
    // 员工请假申请相关API
    
    @PostMapping("/leave-request")
    public ResponseEntity<?> submitLeaveRequest(@Valid @RequestBody LeaveRequest leaveRequest) {
        try {
            // 验证日期
            if (leaveRequest.getStartDate().isBefore(LocalDate.now())) {
                return ResponseEntity.badRequest().body("Start date cannot be in the past");
            }
            if (leaveRequest.getEndDate().isBefore(leaveRequest.getStartDate())) {
                return ResponseEntity.badRequest().body("End date must be after start date");
            }
            
            // 启动工作流
            String processInstanceId = workflowService.startLeaveRequestProcess(leaveRequest);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Leave request submitted successfully");
            response.put("processInstanceId", processInstanceId);
            response.put("requestId", leaveRequest.getId());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error submitting leave request: " + e.getMessage());
        }
    }
    
    @GetMapping("/leave-request/employee/{employeeId}")
    public ResponseEntity<List<LeaveRequest>> getEmployeeLeaveRequests(@PathVariable String employeeId) {
        List<LeaveRequest> requests = leaveRequestRepository.findByEmployeeId(employeeId);
        return ResponseEntity.ok(requests);
    }
    
    @GetMapping("/leave-request/manager/{managerId}")
    public ResponseEntity<List<LeaveRequest>> getManagerLeaveRequests(@PathVariable String managerId) {
        List<LeaveRequest> requests = leaveRequestRepository.findPendingRequestsByManager(managerId);
        return ResponseEntity.ok(requests);
    }
    
    @PostMapping("/leave-request/approve")
    public ResponseEntity<?> approveLeaveRequest(@RequestBody Map<String, Object> request) {
        try {
            String taskId = (String) request.get("taskId");
            String managerId = (String) request.get("managerId");
            String decision = (String) request.get("decision");
            String comment = (String) request.get("comment");
            
            workflowService.approveLeaveRequest(taskId, managerId, decision, comment);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Leave request " + decision.toLowerCase());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error processing leave request: " + e.getMessage());
        }
    }
    
    // 业务申请相关API
    
    @PostMapping("/business-request")
    public ResponseEntity<?> submitBusinessRequest(
            @RequestParam("employeeId") String employeeId,
            @RequestParam("employeeName") String employeeName,
            @RequestParam("businessType") String businessType,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("managerId") String managerId,
            @RequestParam(value = "contractFile", required = false) MultipartFile contractFile) {
        
        try {
            BusinessRequest businessRequest = new BusinessRequest();
            businessRequest.setEmployeeId(employeeId);
            businessRequest.setEmployeeName(employeeName);
            businessRequest.setBusinessType(businessType);
            businessRequest.setTitle(title);
            businessRequest.setDescription(description);
            businessRequest.setManagerId(managerId);
            
            // 处理文件上传
            if (contractFile != null && !contractFile.isEmpty()) {
                String filePath = fileUploadService.uploadFile(contractFile, employeeId);
                businessRequest.setContractFileName(contractFile.getOriginalFilename());
                businessRequest.setContractFilePath(filePath);
                businessRequest.setContractFileSize(contractFile.getSize());
            }
            
            // 启动工作流
            String processInstanceId = workflowService.startBusinessRequestProcess(businessRequest);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Business request submitted successfully");
            response.put("processInstanceId", processInstanceId);
            response.put("requestId", businessRequest.getId());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error submitting business request: " + e.getMessage());
        }
    }
    
    @GetMapping("/business-request/employee/{employeeId}")
    public ResponseEntity<List<BusinessRequest>> getEmployeeBusinessRequests(@PathVariable String employeeId) {
        List<BusinessRequest> requests = businessRequestRepository.findByEmployeeId(employeeId);
        return ResponseEntity.ok(requests);
    }
    
    @GetMapping("/business-request/manager/{managerId}")
    public ResponseEntity<List<BusinessRequest>> getManagerBusinessRequests(@PathVariable String managerId) {
        List<BusinessRequest> requests = businessRequestRepository.findPendingRequestsByManager(managerId);
        return ResponseEntity.ok(requests);
    }
    
    @PostMapping("/business-request/review-document")
    public ResponseEntity<?> reviewDocument(@RequestBody Map<String, Object> request) {
        try {
            String taskId = (String) request.get("taskId");
            String managerId = (String) request.get("managerId");
            Boolean documentReviewed = (Boolean) request.get("documentReviewed");
            
            Map<String, Object> variables = new HashMap<>();
            variables.put("documentReviewed", documentReviewed);
            variables.put("managerId", managerId);
            
            workflowService.completeTask(taskId, variables);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Document review completed");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error reviewing document: " + e.getMessage());
        }
    }
    
    @PostMapping("/business-request/approve")
    public ResponseEntity<?> approveBusinessRequest(@RequestBody Map<String, Object> request) {
        try {
            String taskId = (String) request.get("taskId");
            String managerId = (String) request.get("managerId");
            String decision = (String) request.get("decision");
            String comment = (String) request.get("comment");
            
            workflowService.approveBusinessRequest(taskId, managerId, decision, comment);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Business request " + decision.toLowerCase());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error processing business request: " + e.getMessage());
        }
    }
    
    @GetMapping("/business-request/{id}/download")
    public ResponseEntity<Resource> downloadContractFile(@PathVariable Long id) {
        try {
            Optional<BusinessRequest> requestOpt = businessRequestRepository.findById(id);
            if (!requestOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            BusinessRequest businessRequest = requestOpt.get();
            if (businessRequest.getContractFilePath() == null) {
                return ResponseEntity.notFound().build();
            }
            
            byte[] fileData = fileUploadService.readFile(businessRequest.getContractFilePath());
            ByteArrayResource resource = new ByteArrayResource(fileData);
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + businessRequest.getContractFileName() + "\"")
                    .contentLength(fileData.length)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // 任务管理相关API
    
    @GetMapping("/tasks/user/{userId}")
    public ResponseEntity<List<Task>> getUserTasks(@PathVariable String userId) {
        List<Task> tasks = workflowService.getUserTasks(userId);
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/tasks/candidate/{userId}")
    public ResponseEntity<List<Task>> getUserCandidateTasks(@PathVariable String userId) {
        List<Task> tasks = workflowService.getUserCandidateTasks(userId);
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/tasks/group/{groupId}")
    public ResponseEntity<List<Task>> getGroupCandidateTasks(@PathVariable String groupId) {
        List<Task> tasks = workflowService.getGroupCandidateTasks(groupId);
        return ResponseEntity.ok(tasks);
    }
    
    @PostMapping("/tasks/{taskId}/claim")
    public ResponseEntity<?> claimTask(@PathVariable String taskId, @RequestBody Map<String, String> request) {
        try {
            String userId = request.get("userId");
            workflowService.claimTask(taskId, userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Task claimed successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error claiming task: " + e.getMessage());
        }
    }
    
    @GetMapping("/tasks/{taskId}")
    public ResponseEntity<Task> getTask(@PathVariable String taskId) {
        Task task = workflowService.getTaskById(taskId);
        if (task == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(task);
    }
    
    @GetMapping("/tasks/{taskId}/variables")
    public ResponseEntity<Map<String, Object>> getTaskVariables(@PathVariable String taskId) {
        Map<String, Object> variables = workflowService.getTaskVariables(taskId);
        return ResponseEntity.ok(variables);
    }
}