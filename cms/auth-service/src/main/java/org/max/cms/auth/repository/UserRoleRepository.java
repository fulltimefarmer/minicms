package org.max.cms.auth.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.max.cms.auth.entity.UserRole;

import java.util.List;

@Mapper
public interface UserRoleRepository extends BaseMapper<UserRole> {
    
    List<UserRole> findByUserId(Long userId);
    
    void deleteByUserId(Long userId);
}