package org.max.cms.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.max.cms.common.entity.BaseEntity;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("departments")
public class Department extends BaseEntity {
    
    private String name;
    private String code;
    private String description;
    private Long parentId;
    private String path;
    private Integer level;
    private Integer sort;
    private String manager;
    private String phone;
    private String email;
    private String address;
    private Boolean enabled = true;
    
    // 子部门列表（非数据库字段，用于树形结构）
    private List<Department> children;
    
    // 父部门名称（非数据库字段）
    private String parentName;
}