package org.max.cms.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.max.cms.common.entity.BaseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("salaries")
public class Salary extends BaseEntity {
    
    private Long employeeId; // 员工ID
    private Long jobLevelId; // 职级ID
    private BigDecimal basicSalary; // 基本工资
    private BigDecimal performanceBonus; // 绩效奖金
    private BigDecimal positionAllowance; // 岗位津贴
    private BigDecimal mealAllowance; // 餐补
    private BigDecimal transportAllowance; // 交通补贴
    private BigDecimal housingAllowance; // 住房补贴
    private BigDecimal overtimeAllowance; // 加班费
    private BigDecimal otherAllowance; // 其他津贴
    private BigDecimal socialInsurance; // 社保缴费
    private BigDecimal housingFund; // 公积金缴费
    private BigDecimal personalTax; // 个人所得税
    private BigDecimal otherDeduction; // 其他扣款
    private BigDecimal grossSalary; // 应发工资
    private BigDecimal netSalary; // 实发工资
    private LocalDate effectiveDate; // 生效日期
    private LocalDate endDate; // 结束日期
    private String status; // 状态 ACTIVE/INACTIVE
    private String remark; // 备注
    
    // 关联信息（非数据库字段）
    private Employee employee;
    private JobLevel jobLevel;
}