package com.company.workflow.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "business_requests")
public class BusinessRequest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Employee ID is required")
    @Column(name = "employee_id", nullable = false)
    private String employeeId;
    
    @NotBlank(message = "Employee name is required")
    @Column(name = "employee_name", nullable = false)
    private String employeeName;
    
    @NotBlank(message = "Business type is required")
    @Column(name = "business_type", nullable = false)
    private String businessType;
    
    @NotBlank(message = "Title is required")
    @Column(name = "title", nullable = false)
    private String title;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "contract_file_name")
    private String contractFileName;
    
    @Column(name = "contract_file_path")
    private String contractFilePath;
    
    @Column(name = "contract_file_size")
    private Long contractFileSize;
    
    @Column(name = "status", nullable = false)
    private String status = "PENDING";
    
    @Column(name = "manager_id")
    private String managerId;
    
    @Column(name = "manager_comment")
    private String managerComment;
    
    @Column(name = "process_instance_id")
    private String processInstanceId;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Constructors
    public BusinessRequest() {}
    
    public BusinessRequest(String employeeId, String employeeName, String businessType, 
                          String title, String description) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.businessType = businessType;
        this.title = title;
        this.description = description;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getEmployeeId() {
        return employeeId;
    }
    
    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }
    
    public String getEmployeeName() {
        return employeeName;
    }
    
    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }
    
    public String getBusinessType() {
        return businessType;
    }
    
    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getContractFileName() {
        return contractFileName;
    }
    
    public void setContractFileName(String contractFileName) {
        this.contractFileName = contractFileName;
    }
    
    public String getContractFilePath() {
        return contractFilePath;
    }
    
    public void setContractFilePath(String contractFilePath) {
        this.contractFilePath = contractFilePath;
    }
    
    public Long getContractFileSize() {
        return contractFileSize;
    }
    
    public void setContractFileSize(Long contractFileSize) {
        this.contractFileSize = contractFileSize;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getManagerId() {
        return managerId;
    }
    
    public void setManagerId(String managerId) {
        this.managerId = managerId;
    }
    
    public String getManagerComment() {
        return managerComment;
    }
    
    public void setManagerComment(String managerComment) {
        this.managerComment = managerComment;
    }
    
    public String getProcessInstanceId() {
        return processInstanceId;
    }
    
    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}