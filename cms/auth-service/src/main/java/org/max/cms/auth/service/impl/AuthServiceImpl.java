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

        // 验证密码（明文比较）
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

        // 生成token
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("email", user.getEmail());
        String token = jwtUtil.generateToken(user.getUsername(), claims);

        log.info("User login successful: {}", loginRequest.getUsername());

        return LoginResponse.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .roles(new ArrayList<>()) // TODO: 实现角色查询
                .permissions(new ArrayList<>()) // TODO: 实现权限查询
                .build();
    }

    @Override
    public void logout(String token) {
        // TODO: 实现token黑名单机制
        log.info("User logout");
    }

    @Override
    public boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }

    @Override
    public String refreshToken(String token) {
        String username = jwtUtil.extractUsername(token);
        return jwtUtil.generateToken(username);
    }
}