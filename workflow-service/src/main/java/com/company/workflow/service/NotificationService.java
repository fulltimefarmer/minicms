package com.company.workflow.service;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

@Service("notificationService")
public class NotificationService implements JavaDelegate {
    
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String processDefinitionKey = execution.getProcessDefinitionId();
        String taskDefinitionKey = execution.getCurrentActivityId();
        
        if (processDefinitionKey.contains("leave-request-process")) {
            handleLeaveRequestNotification(execution, taskDefinitionKey);
        } else if (processDefinitionKey.contains("business-request-process")) {
            handleBusinessRequestNotification(execution, taskDefinitionKey);
        }
    }
    
    private void handleLeaveRequestNotification(DelegateExecution execution, String taskDefinitionKey) {
        String employeeName = (String) execution.getVariable("employeeName");
        String employeeId = (String) execution.getVariable("employeeId");
        String managerId = (String) execution.getVariable("managerId");
        String managerComment = (String) execution.getVariable("managerComment");
        
        switch (taskDefinitionKey) {
            case "NotifyApproval":
                sendLeaveApprovalNotification(employeeId, employeeName, managerId, managerComment);
                break;
            case "NotifyRejection":
                sendLeaveRejectionNotification(employeeId, employeeName, managerId, managerComment);
                break;
            default:
                System.out.println("Unknown task definition: " + taskDefinitionKey);
        }
    }
    
    private void handleBusinessRequestNotification(DelegateExecution execution, String taskDefinitionKey) {
        String employeeName = (String) execution.getVariable("employeeName");
        String employeeId = (String) execution.getVariable("employeeId");
        String managerId = (String) execution.getVariable("managerId");
        String managerComment = (String) execution.getVariable("managerComment");
        String title = (String) execution.getVariable("title");
        
        switch (taskDefinitionKey) {
            case "NotifyApproval":
                sendBusinessApprovalNotification(employeeId, employeeName, managerId, managerComment, title);
                break;
            case "NotifyRejection":
                sendBusinessRejectionNotification(employeeId, employeeName, managerId, managerComment, title);
                break;
            case "NotifyDocumentIssue":
                sendDocumentIssueNotification(employeeId, employeeName, title);
                break;
            default:
                System.out.println("Unknown task definition: " + taskDefinitionKey);
        }
    }
    
    private void sendLeaveApprovalNotification(String employeeId, String employeeName, String managerId, String managerComment) {
        System.out.println("=== LEAVE REQUEST APPROVED ===");
        System.out.println("Employee: " + employeeName + " (" + employeeId + ")");
        System.out.println("Manager: " + managerId);
        System.out.println("Comment: " + managerComment);
        System.out.println("Your leave request has been approved!");
        System.out.println("===============================");
        
        // In a real application, you would send email, SMS, or push notification
        // For demo purposes, we're just logging the notification
    }
    
    private void sendLeaveRejectionNotification(String employeeId, String employeeName, String managerId, String managerComment) {
        System.out.println("=== LEAVE REQUEST REJECTED ===");
        System.out.println("Employee: " + employeeName + " (" + employeeId + ")");
        System.out.println("Manager: " + managerId);
        System.out.println("Comment: " + managerComment);
        System.out.println("Your leave request has been rejected.");
        System.out.println("===============================");
        
        // In a real application, you would send email, SMS, or push notification
        // For demo purposes, we're just logging the notification
    }
    
    private void sendBusinessApprovalNotification(String employeeId, String employeeName, String managerId, String managerComment, String title) {
        System.out.println("=== BUSINESS REQUEST APPROVED ===");
        System.out.println("Employee: " + employeeName + " (" + employeeId + ")");
        System.out.println("Manager: " + managerId);
        System.out.println("Request: " + title);
        System.out.println("Comment: " + managerComment);
        System.out.println("Your business request has been approved!");
        System.out.println("==================================");
        
        // In a real application, you would send email, SMS, or push notification
        // For demo purposes, we're just logging the notification
    }
    
    private void sendBusinessRejectionNotification(String employeeId, String employeeName, String managerId, String managerComment, String title) {
        System.out.println("=== BUSINESS REQUEST REJECTED ===");
        System.out.println("Employee: " + employeeName + " (" + employeeId + ")");
        System.out.println("Manager: " + managerId);
        System.out.println("Request: " + title);
        System.out.println("Comment: " + managerComment);
        System.out.println("Your business request has been rejected.");
        System.out.println("==================================");
        
        // In a real application, you would send email, SMS, or push notification
        // For demo purposes, we're just logging the notification
    }
    
    private void sendDocumentIssueNotification(String employeeId, String employeeName, String title) {
        System.out.println("=== DOCUMENT ISSUE NOTIFICATION ===");
        System.out.println("Employee: " + employeeName + " (" + employeeId + ")");
        System.out.println("Request: " + title);
        System.out.println("There is an issue with the submitted document.");
        System.out.println("Please review and resubmit.");
        System.out.println("===================================");
        
        // In a real application, you would send email, SMS, or push notification
        // For demo purposes, we're just logging the notification
    }
}