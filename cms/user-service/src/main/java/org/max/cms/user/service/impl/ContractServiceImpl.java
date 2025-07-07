package org.max.cms.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.max.cms.user.entity.Contract;
import org.max.cms.user.entity.Employee;
import org.max.cms.user.mapper.ContractMapper;
import org.max.cms.user.mapper.EmployeeMapper;
import org.max.cms.user.service.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class ContractServiceImpl implements ContractService {

    @Autowired
    private ContractMapper contractMapper;
    
    @Autowired
    private EmployeeMapper employeeMapper;

    @Override
    public Contract createContract(Contract contract) {
        contractMapper.insert(contract);
        return contract;
    }

    @Override
    public Contract updateContract(Contract contract) {
        contractMapper.updateById(contract);
        return contract;
    }

    @Override
    public void deleteContract(Long id) {
        // 软删除
        Contract contract = new Contract();
        contract.setId(id);
        contract.setDeleted(true);
        contractMapper.updateById(contract);
    }

    @Override
    public Contract getContractById(Long id) {
        Contract contract = contractMapper.selectById(id);
        if (contract != null) {
            loadRelatedData(contract);
        }
        return contract;
    }

    @Override
    public List<Contract> getContractsByEmployeeId(Long employeeId) {
        return contractMapper.findByEmployeeId(employeeId);
    }

    @Override
    public Contract getCurrentContractByEmployeeId(Long employeeId) {
        Contract contract = contractMapper.getCurrentContractByEmployeeId(employeeId);
        if (contract != null) {
            loadRelatedData(contract);
        }
        return contract;
    }

    @Override
    public Contract getContractByContractNumber(String contractNumber) {
        Contract contract = contractMapper.findByContractNumber(contractNumber);
        if (contract != null) {
            loadRelatedData(contract);
        }
        return contract;
    }

    @Override
    public List<Contract> getContractsByStatus(String status) {
        return contractMapper.findByStatus(status);
    }

    @Override
    public List<Contract> getExpiringContracts(LocalDate startDate, LocalDate endDate) {
        return contractMapper.findExpiringContracts(startDate, endDate);
    }

    @Override
    public Contract renewContract(Long contractId, LocalDate newEndDate, String remark) {
        Contract existingContract = contractMapper.selectById(contractId);
        if (existingContract == null) {
            throw new RuntimeException("合同不存在");
        }
        
        // 创建新的合同记录
        Contract newContract = Contract.builder()
                .employeeId(existingContract.getEmployeeId())
                .contractType(existingContract.getContractType())
                .startDate(existingContract.getEndDate().plusDays(1))
                .endDate(newEndDate)
                .workContent(existingContract.getWorkContent())
                .workLocation(existingContract.getWorkLocation())
                .basicSalary(existingContract.getBasicSalary())
                .workingHours(existingContract.getWorkingHours())
                .benefits(existingContract.getBenefits())
                .status("ACTIVE")
                .remark(remark)
                .build();
                
        // 生成新的合同编号
        newContract.setContractNumber(generateContractNumber(existingContract.getEmployeeId()));
        
        // 结束原合同
        existingContract.setStatus("EXPIRED");
        contractMapper.updateById(existingContract);
        
        // 创建新合同
        contractMapper.insert(newContract);
        
        return newContract;
    }

    @Override
    public void terminateContract(Long contractId, LocalDate terminationDate, String reason) {
        Contract contract = contractMapper.selectById(contractId);
        if (contract == null) {
            throw new RuntimeException("合同不存在");
        }
        
        contract.setStatus("TERMINATED");
        contract.setEndDate(terminationDate);
        contract.setRemark(contract.getRemark() + " | 终止原因: " + reason);
        contractMapper.updateById(contract);
    }

    @Override
    public int batchRenewContracts(List<Long> contractIds, LocalDate newEndDate, String remark) {
        int count = 0;
        for (Long contractId : contractIds) {
            try {
                renewContract(contractId, newEndDate, remark);
                count++;
            } catch (Exception e) {
                // 记录错误日志，继续处理下一个
                // log.error("续签合同失败: {}", contractId, e);
            }
        }
        return count;
    }

    @Override
    public List<Contract> getContractExpirationReminders(int daysBeforeExpiration) {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(daysBeforeExpiration);
        return contractMapper.findExpiringContracts(startDate, endDate);
    }
    
    private void loadRelatedData(Contract contract) {
        // 加载员工信息
        if (contract.getEmployeeId() != null) {
            Employee employee = employeeMapper.selectById(contract.getEmployeeId());
            contract.setEmployee(employee);
        }
    }
    
    private String generateContractNumber(Long employeeId) {
        // 生成合同编号：CT + 年份 + 员工ID + 流水号
        String year = String.valueOf(LocalDate.now().getYear());
        String employeeIdStr = String.format("%06d", employeeId);
        
        // 查询该员工今年的合同数量
        QueryWrapper<Contract> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("employee_id", employeeId)
                   .like("contract_number", "CT" + year + employeeIdStr)
                   .eq("deleted", false);
        long count = contractMapper.selectCount(queryWrapper);
        
        String sequence = String.format("%02d", count + 1);
        return "CT" + year + employeeIdStr + sequence;
    }
}