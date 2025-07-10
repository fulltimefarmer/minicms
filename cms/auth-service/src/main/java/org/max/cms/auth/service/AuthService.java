package org.max.cms.auth.service;

import org.max.cms.auth.dto.LoginRequest;
import org.max.cms.auth.dto.LoginResponse;

public interface AuthService {
    
    LoginResponse login(LoginRequest loginRequest);
    
    void logout(String token);
    
    String refreshToken(String token);
}