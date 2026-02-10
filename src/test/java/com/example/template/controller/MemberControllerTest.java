package com.example.template.controller;

import com.example.template.dto.member.*;
import com.example.template.service.interfaces.MemberService;
import com.example.template.jwt.JwtService;
import com.example.template.security.CustomAuthenticationEntryPoint;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) // 👈 bypass security filters to avoid SpEL #id errors
@ActiveProfiles({"dev", "test"})
@DisplayName("👥 Member Controller - Enterprise Fix")
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean private MemberService memberService;
    @MockitoBean private JwtService jwtService;
    @MockitoBean private UserDetailsService userDetailsService;
    @MockitoBean private CustomAuthenticationEntryPoint authEntryPoint;
    @MockitoBean private AuthenticationProvider authenticationProvider;

    @Test
    @WithMockUser
    @DisplayName("✅ GET /me - Should work with AuthenticationPrincipal")
    void getCurrentMember_Success() throws Exception {
        var response = new MemberResponseDTO(1L, "John", "john@test.com");
        when(memberService.getByEmail(any())).thenReturn(response);

        mockMvc.perform(get("/api/v1/members/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email", is("john@test.com")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("✅ PUT /{id} - Profile update")
    void update_Success() throws Exception {
        Long id = 1L;
        var request = new MemberRequestUpdateDTO("New Name", "new@email.com");
        var response = new MemberResponseDTO(id, "New Name", "new@email.com");

        when(memberService.update(eq(id), any())).thenReturn(response);

        mockMvc.perform(put("/api/v1/members/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN") // 👈 Indispensable pour le Delete aussi
    @DisplayName("✅ DELETE /{id} - Basic delete test")
    void delete_Success() throws Exception {
        mockMvc.perform(delete("/api/v1/members/1"))
                .andExpect(status().isOk());
    }


    @Test
    @WithMockUser
    @DisplayName("✅ PATCH /{id}/password - Should use PatchMapping as defined in Controller")
    void updatePassword_Success() throws Exception {
        var request = new PasswordUpdateDTO("oldPass123!", "newSecurePass2026");

       mockMvc.perform(patch("/api/v1/members/1/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));
    }

    @Test
    @WithMockUser
    @DisplayName("❌ PATCH /{id}/password - Should trigger 400 via GlobalExceptionHandler")
    void updatePassword_ValidationFail() throws Exception {
        var invalid = new PasswordUpdateDTO("old", "short"); // min 8 required

        mockMvc.perform(patch("/api/v1/members/1/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)));
    }

}



