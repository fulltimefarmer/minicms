package org.max.cms.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.max.cms.common.entity.BaseEntity;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("job_levels")
public class JobLevel extends BaseEntity {
    
    private String name; // 职级名称
    private String code; // 职级代码
    private String description; // 职级描述
    private Integer level; // 职级等级（数字越大等级越高）
    private BigDecimal minSalary; // 最低薪资
    private BigDecimal maxSalary; // 最高薪资
    private String responsibilities; // 职责描述
    private String requirements; // 任职要求
    private Boolean enabled = true; // 是否启用
    private Integer sort; // 排序
}