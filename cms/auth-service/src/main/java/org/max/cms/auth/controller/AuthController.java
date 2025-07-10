package org.max.cms.auth.controller;

import lombok.RequiredArgsConstructor;
import org.max.cms.auth.dto.ApiResponse;
import org.max.cms.auth.dto.LoginRequest;
import org.max.cms.auth.dto.LoginResponse;
import org.max.cms.auth.service.AuthService;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse response = authService.login(loginRequest);
            return ApiResponse.success(response);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestHeader("Authorization") String token) {
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            authService.logout(token);
            return ApiResponse.success();
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PostMapping("/refresh")
    public ApiResponse<String> refreshToken(@RequestHeader("Authorization") String token) {
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            String newToken = authService.refreshToken(token);
            return ApiResponse.success(newToken);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}