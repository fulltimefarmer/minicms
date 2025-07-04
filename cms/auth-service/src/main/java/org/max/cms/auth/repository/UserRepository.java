package org.max.cms.auth.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.max.cms.auth.entity.User;

import java.util.Optional;

@Mapper
public interface UserRepository extends BaseMapper<User> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
}