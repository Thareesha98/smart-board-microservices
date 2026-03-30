package com.sbms.sbms_backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final GatewayHeaderAuthenticationFilter gatewayHeaderAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            // disable unused features
            .csrf(csrf -> csrf.disable())
            .httpBasic(basic -> basic.disable())
            .formLogin(form -> form.disable())
            .logout(logout -> logout.disable())

            .sessionManagement(sm ->
                sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // register gateway authentication filter
            .addFilterBefore(
                gatewayHeaderAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            )

            .authorizeHttpRequests(auth -> auth

                // ---------------------------
                // ACTUATOR
                // ---------------------------
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers(
                        "/ws", "/ws/**", 
                        "/backend-ws", "/backend-ws/**", 
                        "/backend-ws-sockjs", "/backend-ws-sockjs/**"
                    ).permitAll()

                // ---------------------------
                // PUBLIC ENDPOINTS
                // ---------------------------
                
                .requestMatchers("/api/bills/internal/**").permitAll()
                .requestMatchers(
                        "/api/auth/**",
                        "/internal/**",
                        "/api/boardings",
                        "/api/boardings/**",
                        "/api/maintenance/**",
                        "/api/registrations/**",
                        "/api/files/**",
                        "/ws/**",
                        "/api/users/public/**",
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/api/emergency/**"
                ).permitAll()

                // ---------------------------
                // ADMIN
                // ---------------------------
                .requestMatchers("/api/admin/**")
                .hasRole("ADMIN")

                .requestMatchers("/api/reports/admin/**")
                .hasRole("ADMIN")

                // ---------------------------
                // OWNER
                // ---------------------------
                .requestMatchers("/api/owner/**")
                .hasRole("OWNER")

                .requestMatchers("/api/boardings/owner/**")
                .hasRole("OWNER")

                // ---------------------------
                // STUDENT
                // ---------------------------
                .requestMatchers("/api/student/**")
                .hasRole("STUDENT")

                .requestMatchers("/api/bills/student/**")
                .hasRole("STUDENT")

                .requestMatchers("/api/payments/**")
                .hasRole("STUDENT")

                // ---------------------------
                // SHARED
                // ---------------------------
                .requestMatchers("/api/reports/**")
                .hasAnyRole("STUDENT", "OWNER")

                // ---------------------------
                // EVERYTHING ELSE
                // ---------------------------
                .anyRequest().authenticated()
            );

        return http.build();
    }
}