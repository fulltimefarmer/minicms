package org.max.cms.user.service;

import org.max.cms.user.entity.Salary;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface SalaryService {
    
    /**
     * 创建薪资信息
     */
    Salary createSalary(Salary salary);
    
    /**
     * 更新薪资信息
     */
    Salary updateSalary(Salary salary);
    
    /**
     * 删除薪资信息
     */
    void deleteSalary(Long id);
    
    /**
     * 根据ID获取薪资信息
     */
    Salary getSalaryById(Long id);
    
    /**
     * 获取员工当前薪资信息
     */
    Salary getCurrentSalaryByEmployeeId(Long employeeId);
    
    /**
     * 获取员工薪资历史
     */
    List<Salary> getSalaryHistoryByEmployeeId(Long employeeId);
    
    /**
     * 根据职级获取薪资列表
     */
    List<Salary> getSalariesByJobLevelId(Long jobLevelId);
    
    /**
     * 根据状态获取薪资列表
     */
    List<Salary> getSalariesByStatus(String status);
    
    /**
     * 薪资调整
     */
    Salary adjustSalary(Long employeeId, BigDecimal newBasicSalary, LocalDate effectiveDate, String remark);
    
    /**
     * 批量薪资调整
     */
    int batchAdjustSalary(List<Long> employeeIds, BigDecimal adjustmentAmount, LocalDate effectiveDate, String remark);
    
    /**
     * 计算净工资
     */
    BigDecimal calculateNetSalary(Salary salary);
    
    /**
     * 生成工资单
     */
    Salary generatePayslip(Long employeeId, LocalDate payrollDate);
}