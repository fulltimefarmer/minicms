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
@TableName("user_roles")
public class UserRole extends BaseEntity {
    
    private Long userId;
    private Long roleId;
}