package org.max.cms.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.max.cms.user.entity.Salary;
import org.max.cms.user.entity.Employee;
import org.max.cms.user.entity.JobLevel;
import org.max.cms.user.mapper.SalaryMapper;
import org.max.cms.user.mapper.EmployeeMapper;
import org.max.cms.user.mapper.JobLevelMapper;
import org.max.cms.user.service.SalaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class SalaryServiceImpl implements SalaryService {

    @Autowired
    private SalaryMapper salaryMapper;
    
    @Autowired
    private EmployeeMapper employeeMapper;
    
    @Autowired
    private JobLevelMapper jobLevelMapper;

    @Override
    public Salary createSalary(Salary salary) {
        // 计算净工资
        salary.setNetSalary(calculateNetSalary(salary));
        salaryMapper.insert(salary);
        return salary;
    }

    @Override
    public Salary updateSalary(Salary salary) {
        // 重新计算净工资
        salary.setNetSalary(calculateNetSalary(salary));
        salaryMapper.updateById(salary);
        return salary;
    }

    @Override
    public void deleteSalary(Long id) {
        // 软删除
        Salary salary = new Salary();
        salary.setId(id);
        salary.setDeleted(true);
        salaryMapper.updateById(salary);
    }

    @Override
    public Salary getSalaryById(Long id) {
        Salary salary = salaryMapper.selectById(id);
        if (salary != null) {
            loadRelatedData(salary);
        }
        return salary;
    }

    @Override
    public Salary getCurrentSalaryByEmployeeId(Long employeeId) {
        Salary salary = salaryMapper.getCurrentSalaryByEmployeeId(employeeId);
        if (salary != null) {
            loadRelatedData(salary);
        }
        return salary;
    }

    @Override
    public List<Salary> getSalaryHistoryByEmployeeId(Long employeeId) {
        return salaryMapper.getSalaryHistoryByEmployeeId(employeeId);
    }

    @Override
    public List<Salary> getSalariesByJobLevelId(Long jobLevelId) {
        return salaryMapper.getSalariesByJobLevelId(jobLevelId);
    }

    @Override
    public List<Salary> getSalariesByStatus(String status) {
        return salaryMapper.findByStatus(status);
    }

    @Override
    public Salary adjustSalary(Long employeeId, BigDecimal newBasicSalary, LocalDate effectiveDate, String remark) {
        // 获取当前薪资
        Salary currentSalary = salaryMapper.getCurrentSalaryByEmployeeId(employeeId);
        
        // 结束当前薪资
        if (currentSalary != null) {
            currentSalary.setStatus("INACTIVE");
            currentSalary.setEndDate(effectiveDate.minusDays(1));
            salaryMapper.updateById(currentSalary);
        }
        
        // 创建新薪资记录
        Salary newSalary = Salary.builder()
                .employeeId(employeeId)
                .jobLevelId(currentSalary != null ? currentSalary.getJobLevelId() : null)
                .basicSalary(newBasicSalary)
                .effectiveDate(effectiveDate)
                .status("ACTIVE")
                .remark(remark)
                .build();
                
        // 复制其他津贴信息
        if (currentSalary != null) {
            newSalary.setPerformanceBonus(currentSalary.getPerformanceBonus());
            newSalary.setPositionAllowance(currentSalary.getPositionAllowance());
            newSalary.setMealAllowance(currentSalary.getMealAllowance());
            newSalary.setTransportAllowance(currentSalary.getTransportAllowance());
            newSalary.setHousingAllowance(currentSalary.getHousingAllowance());
            newSalary.setOvertimeAllowance(currentSalary.getOvertimeAllowance());
            newSalary.setOtherAllowance(currentSalary.getOtherAllowance());
            newSalary.setSocialInsurance(currentSalary.getSocialInsurance());
            newSalary.setHousingFund(currentSalary.getHousingFund());
            newSalary.setPersonalTax(currentSalary.getPersonalTax());
            newSalary.setOtherDeduction(currentSalary.getOtherDeduction());
        }
        
        return createSalary(newSalary);
    }

    @Override
    public int batchAdjustSalary(List<Long> employeeIds, BigDecimal adjustmentAmount, LocalDate effectiveDate, String remark) {
        int count = 0;
        for (Long employeeId : employeeIds) {
            try {
                Salary currentSalary = salaryMapper.getCurrentSalaryByEmployeeId(employeeId);
                if (currentSalary != null) {
                    BigDecimal newBasicSalary = currentSalary.getBasicSalary().add(adjustmentAmount);
                    adjustSalary(employeeId, newBasicSalary, effectiveDate, remark);
                    count++;
                }
            } catch (Exception e) {
                // 记录错误日志，继续处理下一个
                // log.error("调整员工薪资失败: {}", employeeId, e);
            }
        }
        return count;
    }

    @Override
    public BigDecimal calculateNetSalary(Salary salary) {
        BigDecimal grossSalary = BigDecimal.ZERO;
        
        // 计算应发工资
        if (salary.getBasicSalary() != null) {
            grossSalary = grossSalary.add(salary.getBasicSalary());
        }
        if (salary.getPerformanceBonus() != null) {
            grossSalary = grossSalary.add(salary.getPerformanceBonus());
        }
        if (salary.getPositionAllowance() != null) {
            grossSalary = grossSalary.add(salary.getPositionAllowance());
        }
        if (salary.getMealAllowance() != null) {
            grossSalary = grossSalary.add(salary.getMealAllowance());
        }
        if (salary.getTransportAllowance() != null) {
            grossSalary = grossSalary.add(salary.getTransportAllowance());
        }
        if (salary.getHousingAllowance() != null) {
            grossSalary = grossSalary.add(salary.getHousingAllowance());
        }
        if (salary.getOvertimeAllowance() != null) {
            grossSalary = grossSalary.add(salary.getOvertimeAllowance());
        }
        if (salary.getOtherAllowance() != null) {
            grossSalary = grossSalary.add(salary.getOtherAllowance());
        }
        
        salary.setGrossSalary(grossSalary);
        
        // 计算实发工资（扣除各项费用）
        BigDecimal netSalary = grossSalary;
        if (salary.getSocialInsurance() != null) {
            netSalary = netSalary.subtract(salary.getSocialInsurance());
        }
        if (salary.getHousingFund() != null) {
            netSalary = netSalary.subtract(salary.getHousingFund());
        }
        if (salary.getPersonalTax() != null) {
            netSalary = netSalary.subtract(salary.getPersonalTax());
        }
        if (salary.getOtherDeduction() != null) {
            netSalary = netSalary.subtract(salary.getOtherDeduction());
        }
        
        return netSalary;
    }

    @Override
    public Salary generatePayslip(Long employeeId, LocalDate payrollDate) {
        // 获取当前薪资信息
        Salary currentSalary = salaryMapper.getCurrentSalaryByEmployeeId(employeeId);
        if (currentSalary == null) {
            throw new RuntimeException("员工薪资信息不存在");
        }
        
        // 创建工资单记录
        Salary payslip = Salary.builder()
                .employeeId(employeeId)
                .jobLevelId(currentSalary.getJobLevelId())
                .basicSalary(currentSalary.getBasicSalary())
                .performanceBonus(currentSalary.getPerformanceBonus())
                .positionAllowance(currentSalary.getPositionAllowance())
                .mealAllowance(currentSalary.getMealAllowance())
                .transportAllowance(currentSalary.getTransportAllowance())
                .housingAllowance(currentSalary.getHousingAllowance())
                .overtimeAllowance(currentSalary.getOvertimeAllowance())
                .otherAllowance(currentSalary.getOtherAllowance())
                .socialInsurance(currentSalary.getSocialInsurance())
                .housingFund(currentSalary.getHousingFund())
                .personalTax(currentSalary.getPersonalTax())
                .otherDeduction(currentSalary.getOtherDeduction())
                .effectiveDate(payrollDate)
                .status("ACTIVE")
                .remark("工资单-" + payrollDate.toString())
                .build();
                
        return createSalary(payslip);
    }
    
    private void loadRelatedData(Salary salary) {
        // 加载员工信息
        if (salary.getEmployeeId() != null) {
            Employee employee = employeeMapper.selectById(salary.getEmployeeId());
            salary.setEmployee(employee);
        }
        
        // 加载职级信息
        if (salary.getJobLevelId() != null) {
            JobLevel jobLevel = jobLevelMapper.selectById(salary.getJobLevelId());
            salary.setJobLevel(jobLevel);
        }
    }
}