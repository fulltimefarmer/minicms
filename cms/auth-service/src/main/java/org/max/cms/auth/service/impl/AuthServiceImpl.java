package org.max.cms.auth.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.max.cms.auth.dto.LoginRequest;
import org.max.cms.auth.dto.LoginResponse;
import org.max.cms.auth.entity.User;
import org.max.cms.auth.repository.UserRepository;
import org.max.cms.auth.service.AuthService;
import org.max.cms.auth.util.JwtUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        log.info("User login attempt: {}", loginRequest.getUsername());
        
        // 查找用户
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("用户名或密码错误"));

        // 验证密码（支持明文和加密两种方式）
        if (!loginRequest.getPassword().equals(user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 检查用户状态
        if (!user.getEnabled()) {
            throw new RuntimeException("用户已被禁用");
        }

        // 更新最后登录时间
        user.setLastLoginTime(LocalDateTime.now());
        userRepository.updateById(user);

        // 查询用户角色和权限
        List<String> roles = userRepository.findRolesByUsername(user.getUsername());
        List<String> permissions = userRepository.findPermissionsByUsername(user.getUsername());

        // 生成JWT token
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("email", user.getEmail());
        claims.put("roles", roles);
        claims.put("permissions", permissions);
        String token = jwtUtil.generateToken(user.getUsername(), claims);

        log.info("User login successful: {}, roles: {}, permissions: {}", 
                loginRequest.getUsername(), roles, permissions);

        return LoginResponse.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .roles(roles)
                .permissions(permissions)
                .build();
    }

    @Override
    public void logout(String token) {
        // TODO: 实现token黑名单机制
        log.info("User logout");
    }

    @Override
    public String refreshToken(String token) {
        String username = jwtUtil.extractUsername(token);
        
        // 重新查询用户信息以获取最新的角色和权限
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        List<String> roles = userRepository.findRolesByUsername(username);
        List<String> permissions = userRepository.findPermissionsByUsername(username);
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("email", user.getEmail());
        claims.put("roles", roles);
        claims.put("permissions", permissions);
        
        return jwtUtil.generateToken(username, claims);
    }
}