package com.company.workflow.repository;

import com.company.workflow.model.BusinessRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BusinessRequestRepository extends JpaRepository<BusinessRequest, Long> {
    
    List<BusinessRequest> findByEmployeeId(String employeeId);
    
    List<BusinessRequest> findByManagerId(String managerId);
    
    List<BusinessRequest> findByStatus(String status);
    
    Optional<BusinessRequest> findByProcessInstanceId(String processInstanceId);
    
    @Query("SELECT br FROM BusinessRequest br WHERE br.status = 'PENDING' AND br.managerId = ?1")
    List<BusinessRequest> findPendingRequestsByManager(String managerId);
    
    @Query("SELECT br FROM BusinessRequest br WHERE br.employeeId = ?1 AND br.status = ?2")
    List<BusinessRequest> findByEmployeeIdAndStatus(String employeeId, String status);
    
    List<BusinessRequest> findByBusinessType(String businessType);
}