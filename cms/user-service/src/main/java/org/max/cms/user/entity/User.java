package org.max.cms.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.max.cms.common.entity.BaseEntity;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("users")
public class User extends BaseEntity {
    
    private String username;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String phone;
    private String avatar;
    private Boolean enabled = true;
    private Boolean accountNonExpired = true;
    private Boolean accountNonLocked = true;
    private Boolean credentialsNonExpired = true;
    private LocalDateTime lastLoginTime;
    private String lastLoginIp;
    private Integer failedLoginAttempts = 0;
    private LocalDateTime lockedUntil;
    
    // 部门关联
    private Long departmentId;
    private String departmentName; // 非数据库字段
}