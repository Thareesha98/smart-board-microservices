package com.sbms.chat_service.config;



import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;





@Configuration
public class SecurityConfig {
	
	
	

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            .authorizeHttpRequests(auth -> auth
            		
            		

                    // ✅ Allow ALL WebSocket handshake endpoints
                    .requestMatchers("/ws/**").permitAll()

                    .requestMatchers("/topic/**").permitAll()
                    .requestMatchers("/app/**").permitAll()

                    .requestMatchers("/api/**").permitAll()

                    .anyRequest().permitAll()
            );
        
        
        
        

        return http.build();
    }
}