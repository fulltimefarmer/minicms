package org.max.cms.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.max.cms.common.entity.BaseEntity;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("contacts")
public class Contact extends BaseEntity {
    
    private Long employeeId; // 员工ID
    private String contactType; // 联系方式类型 HOME/WORK/EMERGENCY
    private String address; // 地址
    private String city; // 城市
    private String province; // 省份
    private String postalCode; // 邮政编码
    private String country; // 国家
    private String homePhone; // 家庭电话
    private String workPhone; // 工作电话
    private String mobilePhone; // 手机号码
    private String email; // 邮箱
    private String qq; // QQ号
    private String wechat; // 微信号
    private String emergencyContactName; // 紧急联系人姓名
    private String emergencyContactPhone; // 紧急联系人电话
    private String emergencyContactRelation; // 紧急联系人关系
    private Boolean isPrimary = false; // 是否主要联系方式
    private String remark; // 备注
    
    // 关联信息（非数据库字段）
    private Employee employee;
}