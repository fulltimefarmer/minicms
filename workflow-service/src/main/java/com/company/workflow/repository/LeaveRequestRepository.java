package com.company.workflow.repository;

import com.company.workflow.model.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    
    List<LeaveRequest> findByEmployeeId(String employeeId);
    
    List<LeaveRequest> findByManagerId(String managerId);
    
    List<LeaveRequest> findByStatus(String status);
    
    Optional<LeaveRequest> findByProcessInstanceId(String processInstanceId);
    
    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.status = 'PENDING' AND lr.managerId = ?1")
    List<LeaveRequest> findPendingRequestsByManager(String managerId);
    
    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.employeeId = ?1 AND lr.status = ?2")
    List<LeaveRequest> findByEmployeeIdAndStatus(String employeeId, String status);
}