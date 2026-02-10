package com.example.template.controller;

import com.example.template.auth.dto.AuthResponseDTO;
import com.example.template.auth.dto.LoginAuthRequestDTO;
import com.example.template.auth.dto.RegisterAuthRequestDTO;
import com.example.template.auth.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) // Disable Security Filters to isolate Controller logic
@ActiveProfiles("dev")
@DisplayName("🛡️ AuthController Debugged Suite")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @Test
    @DisplayName("POST /register - Success Case")
    void register_Success() throws Exception {
        var request = new RegisterAuthRequestDTO("John Doe", "john@test.com", "password123!");
        var mockResponse = new AuthResponseDTO("token", "john@test.com", List.of("USER"));

        when(authService.register(any())).thenReturn(mockResponse);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print()) // This will show the real JSON in your console if it fails
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.email", is("john@test.com")));
    }

    @Test
    @DisplayName("POST /login - Success Case")
    void login_Success() throws Exception {
        var request = new LoginAuthRequestDTO("admin@test.com", "password123!");
        var mockResponse = new AuthResponseDTO("token", "admin@test.com", List.of("ADMIN"));

        when(authService.login(any())).thenReturn(mockResponse);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));
    }

    @Test
    @DisplayName("POST /login - Should fail when password is too short")
    void login_Validation_Fail() throws Exception {
        var invalidRequest = new LoginAuthRequestDTO("admin@test.com", "123"); // 3 chars < 8

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("at least 8 characters")));
    }


}


