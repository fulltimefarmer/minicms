package org.max.cms.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.max.cms.user.entity.User;
import org.max.cms.user.mapper.UserMapper;
import org.max.cms.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User createUser(User user) {
        userMapper.insert(user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        userMapper.updateById(user);
        return user;
    }

    @Override
    public void deleteUser(Long id) {
        // 软删除
        User user = new User();
        user.setId(id);
        user.setDeleted(true);
        userMapper.updateById(user);
    }

    @Override
    public User getUserById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public List<User> getAllUsers() {
        return userMapper.selectList(
            new QueryWrapper<User>()
                .eq("deleted", false)
                .orderByDesc("created_at")
        );
    }

    @Override
    public User findByUsername(String username) {
        return userMapper.selectOne(
            new QueryWrapper<User>()
                .eq("username", username)
                .eq("deleted", false)
        );
    }

    @Override
    public User findByEmail(String email) {
        return userMapper.selectOne(
            new QueryWrapper<User>()
                .eq("email", email)
                .eq("deleted", false)
        );
    }

    @Override
    public List<User> getUsersByDepartmentId(Long departmentId) {
        return userMapper.selectList(
            new QueryWrapper<User>()
                .eq("department_id", departmentId)
                .eq("deleted", false)
                .orderByDesc("created_at")
        );
    }
}