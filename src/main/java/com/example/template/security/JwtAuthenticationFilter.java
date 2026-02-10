package com.example.template.security;

import com.example.template.jwt.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter that intercepts every request to validate JWT and load authorities.
 * Standard 2026 implementation for Role-Based Access Control (RBAC).
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // 1. Skip filter if no Bearer token is found
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        userEmail = jwtService.extractSubject(jwt);

        // 2. Process authentication if email is present and not already authenticated
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Loading UserDetails (including roles) from the database
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            // 3. If token is valid, establish security context with roles
            if (jwtService.isTokenValid(jwt)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities() // 👈 CRITICAL: Injects roles into the security context
                );

                // Attach request details (IP, browser info) to the authentication object
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 4. Update the SecurityContextHolder
                // From this point, @PreAuthorize can see the user's roles
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 5. Continue to the next filter in the chain
        filterChain.doFilter(request, response);
    }
}
