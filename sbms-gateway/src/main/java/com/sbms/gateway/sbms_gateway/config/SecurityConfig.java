package com.sbms.gateway.sbms_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(ServerHttpSecurity.CorsSpec::disable)

                .authorizeExchange(auth -> auth
                        // 🔓 OPEN ENDPOINTS (NO JWT REQUIRED)
                        .pathMatchers(
                                "/api/auth/**",
                                "/auth/**",
                                "/actuator/**",
                                "/internal-debug/**"
                        ).permitAll()

                        // 🔐 EVERYTHING ELSE MUST HAVE JWT
                        .anyExchange().authenticated()
                )

                // ✔️ Correct, safe, modern JWT handling (reactive)
                .oauth2ResourceServer(oauth -> oauth.jwt())

                .build();
    }
}
