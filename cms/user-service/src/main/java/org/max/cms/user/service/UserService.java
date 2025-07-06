package org.max.cms.user.service;

import org.max.cms.user.entity.User;
import java.util.List;

public interface UserService {
    
    /**
     * 创建用户
     */
    User createUser(User user);
    
    /**
     * 更新用户
     */
    User updateUser(User user);
    
    /**
     * 删除用户
     */
    void deleteUser(Long id);
    
    /**
     * 根据ID获取用户
     */
    User getUserById(Long id);
    
    /**
     * 获取所有用户
     */
    List<User> getAllUsers();
    
    /**
     * 根据用户名查找用户
     */
    User findByUsername(String username);
    
    /**
     * 根据邮箱查找用户
     */
    User findByEmail(String email);
    
    /**
     * 根据部门ID获取用户列表
     */
    List<User> getUsersByDepartmentId(Long departmentId);
}