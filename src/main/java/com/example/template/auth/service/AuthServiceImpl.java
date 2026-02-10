package com.example.template.auth.service;

import com.example.template.auth.dto.*;
import com.example.template.exception.AuthException;
import com.example.template.model.Member;
import com.example.template.model.enums.AppRole;
import com.example.template.repository.MemberRepository;
import com.example.template.jwt.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Authentication Service Implementation - Enterprise Standard 2026.
 * Handles user onboarding and secure login flows.
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * Constructor injection with @Lazy to prevent circular dependency
     * between SecurityConfig and AuthService.
     */
    public AuthServiceImpl(MemberRepository memberRepository,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService,
                           @Lazy AuthenticationManager authenticationManager) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    @Transactional
    public AuthResponseDTO register(RegisterAuthRequestDTO request) {
        if (memberRepository.existsByEmail(request.email())) {
            log.warn("Registration failed: Email {} already in use", request.email());
            throw new AuthException("User already exists with this email");
        }

        Member newMember = Member.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .roles(Set.of(AppRole.ROLE_USER)) // Standard 2026: Default role assignment
                .build();

        memberRepository.save(newMember);
        log.info("New user registered successfully: {}", newMember.getEmail());

        String token = generateTokenWithRoles(newMember);

        return new AuthResponseDTO(
                token,
                newMember.getEmail(),
                newMember.getRoles().stream().map(Enum::name).collect(Collectors.toList())
        );
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponseDTO login(LoginAuthRequestDTO request) {
        try {
            // 1. Authenticate credentials via Spring Security Manager
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );
        } catch (AuthenticationException e) {
            log.warn("Authentication failed for user: {}", request.email());
            throw new AuthException("Invalid email or password");
        }

        // 2. Fetch user to retrieve assigned roles
        Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(() -> {
                    log.error("Consistency error: Authenticated user {} not found in DB", request.email());
                    return new AuthException("User data not found");
                });

        // 3. Issue JWT with roles embedded in extra claims
        String token = generateTokenWithRoles(member);
        log.info("User {} successfully authenticated", member.getEmail());

        return new AuthResponseDTO(
                token,
                member.getEmail(),
                member.getRoles().stream().map(Enum::name).collect(Collectors.toList())
        );
    }

    /**
     * Helper method to package roles into JWT extra claims.
     * This ensures the token is self-contained for stateless authorization.
     */
    private String generateTokenWithRoles(Member member) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("roles", member.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.toList()));

        return jwtService.generateToken(extraClaims, member.getEmail());
    }
}
