package com.example.template.service;

import com.example.template.dto.member.MemberRequestDTO;
import com.example.template.dto.member.MemberResponseDTO;
import com.example.template.exception.AlreadyExistsException;
import com.example.template.mapper.MemberMapper;
import com.example.template.model.Member;
import com.example.template.repository.MemberRepository;
import com.example.template.service.impl.MemberServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock private MemberRepository memberRepository;
    @Mock private MemberMapper memberMapper;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks private MemberServiceImpl memberService;

    @Test
    @DisplayName("Should encode password and save member when email is unique")
    void shouldCreateMemberSuccessfully() {
        // Arrange
        MemberRequestDTO request = new MemberRequestDTO("John", "john@test.com", "plainPassword");
        Member member = Member.builder().email("john@test.com").build();

        when(memberRepository.existsByEmail(anyString())).thenReturn(false);
        when(memberMapper.toEntity(any())).thenReturn(member);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(memberRepository.save(any())).thenReturn(member);
        when(memberMapper.toResponse(any())).thenReturn(new MemberResponseDTO(1L, "John", "john@test.com"));

        // Act
        MemberResponseDTO result = memberService.create(request);

        // Assert
        assertThat(result.email()).isEqualTo("john@test.com");
        verify(passwordEncoder).encode("plainPassword"); // Verify password was hashed
        verify(memberRepository).save(any(Member.class));
    }

    @Test
    @DisplayName("Should throw exception when creating member with existing email")
    void shouldThrowExceptionWhenEmailExists() {
        // Arrange
        MemberRequestDTO request = new MemberRequestDTO("John", "exists@test.com", "pass");
        when(memberRepository.existsByEmail("exists@test.com")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> memberService.create(request))
                .isInstanceOf(AlreadyExistsException.class);
        verify(memberRepository, never()).save(any());
    }
}

