package com.interview.platform.account.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity // Enables annotations like @PreAuthorize on methods
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Why this exists:
        // Centralized API security policy avoids inconsistent endpoint-level protection.
        // Interview talking point:
        // "I enforce authentication globally, then tighten authorization at route/method level."
        return http
            .csrf(csrf -> csrf.disable()) // API is stateless with JWT, so CSRF token is not required
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/**").permitAll() // Health endpoints are public for probes
                .requestMatchers("/api/v1/transfers/**").hasAuthority("SCOPE_transfer.write")
                .anyRequest().authenticated())
            .oauth2ResourceServer(oauth -> oauth.jwt()) // Validates JWT from issuer
            .build();
    }
}
