package org.max.cms.auth.entity;

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
@TableName("permissions")
public class Permission extends BaseEntity {
    
    private String name;
    private String code;
    private String resource;
    private String action;
    private String description;
    private Long parentId;
    private String type; // MENU, BUTTON, API
    private String path;
    private Integer sort;
    private Boolean enabled = true;
}