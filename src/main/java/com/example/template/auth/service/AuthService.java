package com.example.template.auth.service;

import com.example.template.auth.dto.LoginAuthRequestDTO;
import com.example.template.auth.dto.RegisterAuthRequestDTO;
import com.example.template.auth.dto.AuthResponseDTO;

public interface AuthService {
    AuthResponseDTO register(RegisterAuthRequestDTO request);
    AuthResponseDTO login(LoginAuthRequestDTO request);
}
