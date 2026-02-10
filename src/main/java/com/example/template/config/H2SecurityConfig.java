package com.example.template.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Profile("dev & !test")
@Order(1) // Priority over global SecurityConfig
public class H2SecurityConfig {

    @Bean
    public SecurityFilterChain h2SecurityFilterChain(HttpSecurity http) throws Exception {
        http
                // Automatically matches H2 Console path from application properties
                .securityMatcher(PathRequest.toH2Console())

                .csrf(AbstractHttpConfigurer::disable)

                // Required to allow the H2 multi-frame interface while blocking external clickjacking
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))

                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

        return http.build();
    }
}
