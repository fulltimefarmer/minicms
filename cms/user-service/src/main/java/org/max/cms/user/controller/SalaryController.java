package org.max.cms.user.controller;

import org.max.cms.user.entity.Salary;
import org.max.cms.user.service.SalaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/salaries")
@CrossOrigin(origins = "*")
public class SalaryController {

    @Autowired
    private SalaryService salaryService;

    /**
     * 根据ID获取薪资信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<Salary> getSalaryById(@PathVariable Long id) {
        Salary salary = salaryService.getSalaryById(id);
        if (salary != null) {
            return ResponseEntity.ok(salary);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 获取员工当前薪资信息
     */
    @GetMapping("/employee/{employeeId}/current")
    public ResponseEntity<Salary> getCurrentSalaryByEmployeeId(@PathVariable Long employeeId) {
        Salary salary = salaryService.getCurrentSalaryByEmployeeId(employeeId);
        if (salary != null) {
            return ResponseEntity.ok(salary);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 获取员工薪资历史
     */
    @GetMapping("/employee/{employeeId}/history")
    public ResponseEntity<List<Salary>> getSalaryHistoryByEmployeeId(@PathVariable Long employeeId) {
        List<Salary> salaries = salaryService.getSalaryHistoryByEmployeeId(employeeId);
        return ResponseEntity.ok(salaries);
    }

    /**
     * 根据职级获取薪资列表
     */
    @GetMapping("/job-level/{jobLevelId}")
    public ResponseEntity<List<Salary>> getSalariesByJobLevelId(@PathVariable Long jobLevelId) {
        List<Salary> salaries = salaryService.getSalariesByJobLevelId(jobLevelId);
        return ResponseEntity.ok(salaries);
    }

    /**
     * 根据状态获取薪资列表
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Salary>> getSalariesByStatus(@PathVariable String status) {
        List<Salary> salaries = salaryService.getSalariesByStatus(status);
        return ResponseEntity.ok(salaries);
    }

    /**
     * 创建薪资信息
     */
    @PostMapping
    public ResponseEntity<Salary> createSalary(@RequestBody Salary salary) {
        try {
            Salary created = salaryService.createSalary(salary);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 更新薪资信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<Salary> updateSalary(@PathVariable Long id, @RequestBody Salary salary) {
        try {
            salary.setId(id);
            Salary updated = salaryService.updateSalary(salary);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 删除薪资信息
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSalary(@PathVariable Long id) {
        try {
            salaryService.deleteSalary(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 薪资调整
     */
    @PostMapping("/adjust")
    public ResponseEntity<Salary> adjustSalary(
            @RequestParam Long employeeId,
            @RequestParam BigDecimal newBasicSalary,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate effectiveDate,
            @RequestParam(required = false) String remark) {
        try {
            Salary adjusted = salaryService.adjustSalary(employeeId, newBasicSalary, effectiveDate, remark);
            return ResponseEntity.ok(adjusted);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 批量薪资调整
     */
    @PostMapping("/batch-adjust")
    public ResponseEntity<String> batchAdjustSalary(
            @RequestParam List<Long> employeeIds,
            @RequestParam BigDecimal adjustmentAmount,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate effectiveDate,
            @RequestParam(required = false) String remark) {
        try {
            int count = salaryService.batchAdjustSalary(employeeIds, adjustmentAmount, effectiveDate, remark);
            return ResponseEntity.ok("成功调整 " + count + " 个员工的薪资");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("批量调整失败: " + e.getMessage());
        }
    }

    /**
     * 计算净工资
     */
    @PostMapping("/calculate")
    public ResponseEntity<BigDecimal> calculateNetSalary(@RequestBody Salary salary) {
        try {
            BigDecimal netSalary = salaryService.calculateNetSalary(salary);
            return ResponseEntity.ok(netSalary);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 生成工资单
     */
    @PostMapping("/payslip")
    public ResponseEntity<Salary> generatePayslip(
            @RequestParam Long employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate payrollDate) {
        try {
            Salary payslip = salaryService.generatePayslip(employeeId, payrollDate);
            return ResponseEntity.ok(payslip);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}