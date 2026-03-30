package com.sbms.gateway.sbms_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                //  ENABLE CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                .authorizeExchange(auth -> auth

                        // allow preflight requests
                        .pathMatchers(HttpMethod.OPTIONS).permitAll()
                        .pathMatchers("/ws/**").permitAll()

                        // PUBLIC
                        .pathMatchers(
                                "/api/auth/**",
                                "/auth/**",
                                "/actuator/**",
                                "/internal-debug/**",
                                "/ws/**"
                        ).permitAll()
                        
                       

                        // everything else secured
                        .anyExchange().authenticated()
                )

                .oauth2ResourceServer(oauth -> oauth.jwt())

                .build();
    }

    // CORS CONFIG
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "http://sbms.thareesha.software"
        ));

        config.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));

        config.setAllowedHeaders(List.of("*"));

        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);

        return source;
    }
}