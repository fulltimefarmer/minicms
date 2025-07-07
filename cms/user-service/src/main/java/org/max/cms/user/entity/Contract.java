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
@TableName("contracts")
public class Contract extends BaseEntity {
    
    private Long employeeId; // 员工ID
    private String contractNumber; // 合同编号
    private String contractType; // 合同类型 FIXED_TERM/INDEFINITE/TEMPORARY
    private LocalDate startDate; // 合同开始日期
    private LocalDate endDate; // 合同结束日期
    private String workContent; // 工作内容
    private String workLocation; // 工作地点
    private BigDecimal basicSalary; // 基本工资
    private String workingHours; // 工作时间
    private String probationPeriod; // 试用期
    private BigDecimal probationSalary; // 试用期工资
    private String benefits; // 福利待遇
    private String terminationConditions; // 解除条件
    private String renewalConditions; // 续签条件
    private String status; // 合同状态 ACTIVE/EXPIRED/TERMINATED
    private String filePath; // 合同文件路径
    private String signLocation; // 签署地点
    private LocalDate signDate; // 签署日期
    private String remark; // 备注
    
    // 关联信息（非数据库字段）
    private Employee employee;
}