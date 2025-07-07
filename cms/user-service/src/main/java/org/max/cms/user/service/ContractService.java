package org.max.cms.user.service;

import org.max.cms.user.entity.Contract;
import java.time.LocalDate;
import java.util.List;

public interface ContractService {
    
    /**
     * 创建劳动合同
     */
    Contract createContract(Contract contract);
    
    /**
     * 更新劳动合同
     */
    Contract updateContract(Contract contract);
    
    /**
     * 删除劳动合同
     */
    void deleteContract(Long id);
    
    /**
     * 根据ID获取劳动合同
     */
    Contract getContractById(Long id);
    
    /**
     * 根据员工ID获取所有合同
     */
    List<Contract> getContractsByEmployeeId(Long employeeId);
    
    /**
     * 获取员工当前有效合同
     */
    Contract getCurrentContractByEmployeeId(Long employeeId);
    
    /**
     * 根据合同编号获取合同
     */
    Contract getContractByContractNumber(String contractNumber);
    
    /**
     * 根据状态获取合同列表
     */
    List<Contract> getContractsByStatus(String status);
    
    /**
     * 获取即将到期的合同
     */
    List<Contract> getExpiringContracts(LocalDate startDate, LocalDate endDate);
    
    /**
     * 合同续签
     */
    Contract renewContract(Long contractId, LocalDate newEndDate, String remark);
    
    /**
     * 合同终止
     */
    void terminateContract(Long contractId, LocalDate terminationDate, String reason);
    
    /**
     * 批量合同续签
     */
    int batchRenewContracts(List<Long> contractIds, LocalDate newEndDate, String remark);
    
    /**
     * 获取合同到期提醒列表
     */
    List<Contract> getContractExpirationReminders(int daysBeforeExpiration);
}