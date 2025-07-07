package org.max.cms.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.max.cms.common.entity.BaseEntity;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("employees")
public class Employee extends BaseEntity {
    
    private Long userId; // 关联用户ID
    private String employeeNumber; // 工号
    private String idCard; // 身份证号
    private LocalDate birthDate; // 生日
    private String gender; // 性别 M/F
    private String maritalStatus; // 婚姻状况
    private String education; // 学历
    private String emergencyContact; // 紧急联系人
    private String emergencyPhone; // 紧急联系电话
    private LocalDate hireDate; // 入职日期
    private LocalDate probationEndDate; // 试用期结束日期
    private String status; // 员工状态 ACTIVE/INACTIVE/TERMINATED
    private String workLocation; // 工作地点
    private String bankAccount; // 银行账号
    private String bankName; // 开户银行
    private String notes; // 备注
    
    // 关联信息（非数据库字段）
    @TableField(exist = false)
    private User user; // 用户信息
    @TableField(exist = false)
    private JobLevel jobLevel; // 职级信息
    @TableField(exist = false)
    private Salary currentSalary; // 当前薪资信息
    @TableField(exist = false)
    private Contact contact; // 联系方式
}