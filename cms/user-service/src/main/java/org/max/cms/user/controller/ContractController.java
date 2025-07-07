package org.max.cms.user.controller;

import org.max.cms.user.entity.Contract;
import org.max.cms.user.service.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/contracts")
@CrossOrigin(origins = "*")
public class ContractController {

    @Autowired
    private ContractService contractService;

    /**
     * 根据ID获取劳动合同
     */
    @GetMapping("/{id}")
    public ResponseEntity<Contract> getContractById(@PathVariable Long id) {
        Contract contract = contractService.getContractById(id);
        if (contract != null) {
            return ResponseEntity.ok(contract);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 根据员工ID获取所有合同
     */
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<Contract>> getContractsByEmployeeId(@PathVariable Long employeeId) {
        List<Contract> contracts = contractService.getContractsByEmployeeId(employeeId);
        return ResponseEntity.ok(contracts);
    }

    /**
     * 获取员工当前有效合同
     */
    @GetMapping("/employee/{employeeId}/current")
    public ResponseEntity<Contract> getCurrentContractByEmployeeId(@PathVariable Long employeeId) {
        Contract contract = contractService.getCurrentContractByEmployeeId(employeeId);
        if (contract != null) {
            return ResponseEntity.ok(contract);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 根据合同编号获取合同
     */
    @GetMapping("/number/{contractNumber}")
    public ResponseEntity<Contract> getContractByContractNumber(@PathVariable String contractNumber) {
        Contract contract = contractService.getContractByContractNumber(contractNumber);
        if (contract != null) {
            return ResponseEntity.ok(contract);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 根据状态获取合同列表
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Contract>> getContractsByStatus(@PathVariable String status) {
        List<Contract> contracts = contractService.getContractsByStatus(status);
        return ResponseEntity.ok(contracts);
    }

    /**
     * 获取即将到期的合同
     */
    @GetMapping("/expiring")
    public ResponseEntity<List<Contract>> getExpiringContracts(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Contract> contracts = contractService.getExpiringContracts(startDate, endDate);
        return ResponseEntity.ok(contracts);
    }

    /**
     * 获取合同到期提醒列表
     */
    @GetMapping("/reminders")
    public ResponseEntity<List<Contract>> getContractExpirationReminders(
            @RequestParam(defaultValue = "30") int daysBeforeExpiration) {
        List<Contract> contracts = contractService.getContractExpirationReminders(daysBeforeExpiration);
        return ResponseEntity.ok(contracts);
    }

    /**
     * 创建劳动合同
     */
    @PostMapping
    public ResponseEntity<Contract> createContract(@RequestBody Contract contract) {
        try {
            Contract created = contractService.createContract(contract);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 更新劳动合同
     */
    @PutMapping("/{id}")
    public ResponseEntity<Contract> updateContract(@PathVariable Long id, @RequestBody Contract contract) {
        try {
            contract.setId(id);
            Contract updated = contractService.updateContract(contract);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 删除劳动合同
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContract(@PathVariable Long id) {
        try {
            contractService.deleteContract(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 合同续签
     */
    @PostMapping("/{id}/renew")
    public ResponseEntity<Contract> renewContract(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate newEndDate,
            @RequestParam(required = false) String remark) {
        try {
            Contract renewed = contractService.renewContract(id, newEndDate, remark);
            return ResponseEntity.ok(renewed);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 合同终止
     */
    @PostMapping("/{id}/terminate")
    public ResponseEntity<Void> terminateContract(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate terminationDate,
            @RequestParam String reason) {
        try {
            contractService.terminateContract(id, terminationDate, reason);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 批量合同续签
     */
    @PostMapping("/batch-renew")
    public ResponseEntity<String> batchRenewContracts(
            @RequestParam List<Long> contractIds,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate newEndDate,
            @RequestParam(required = false) String remark) {
        try {
            int count = contractService.batchRenewContracts(contractIds, newEndDate, remark);
            return ResponseEntity.ok("成功续签 " + count + " 个合同");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("批量续签失败: " + e.getMessage());
        }
    }
}