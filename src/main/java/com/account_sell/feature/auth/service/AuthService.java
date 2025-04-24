package com.account_sell.feature.auth.service;

import com.account_sell.feature.auth.dto.request.LoginRequestDto;
import com.account_sell.feature.auth.dto.request.RegisterRequestDto;
import com.account_sell.feature.auth.dto.response.AuthResponseDTO;
import com.account_sell.feature.auth.dto.response.UserResponseDto;

import java.util.List;
import java.util.Map;

public interface AuthService {
    AuthResponseDTO login(LoginRequestDto loginDto);

    UserResponseDto register(RegisterRequestDto registerDto);

    UserResponseDto createUserByAdmin(RegisterRequestDto registerDto);

    List<Map<String, Object>> getAvailableRoles();

    boolean validateToken();
}