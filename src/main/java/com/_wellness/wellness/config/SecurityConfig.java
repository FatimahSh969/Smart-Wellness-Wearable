
package com._wellness.wellness.config;

import com._wellness.wellness.security.JwtAuthenticationFilter;

import com._wellness.wellness.security.AuditAccessDeniedHandler;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
@Configuration
@EnableMethodSecurity    
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuditAccessDeniedHandler auditAccessDeniedHandler;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            AuditAccessDeniedHandler auditAccessDeniedHandler) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.auditAccessDeniedHandler = auditAccessDeniedHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .headers(headers ->
                headers.frameOptions(frame -> frame.disable()))

            .httpBasic(basic -> basic.disable())

            .formLogin(form -> form.disable())

            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS))

            .exceptionHandling(exceptions -> exceptions

                .authenticationEntryPoint((request, response, exception) ->
                    response.sendError(
                        HttpStatus.UNAUTHORIZED.value(),
                        "Authentication required"))

                .accessDeniedHandler(auditAccessDeniedHandler))

            .authorizeHttpRequests(auth -> auth

                // Public endpoints
               .requestMatchers(
    "/api/auth/register",
    "/api/auth/login",
    "/api/sensors/readings",   // <-- أضيفي هذا (للاختبار فقط)
    "/h2-console/**",
    "/error"
).permitAll()

                // Admin-only endpoints
                .requestMatchers("/api/admin/**")
                    .hasRole("ADMIN")

                // Regular user endpoints
                .requestMatchers("/api/user/**")
                    .hasRole("USER")

                // Other endpoints require authentication
                .anyRequest().authenticated()
            )

            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
