package com.sbms.sbms_backend.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.*;

import java.util.List;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

   

    // ---------------------------------------------------------
    // Security Filter Chain
    // ---------------------------------------------------------
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(sm ->
                    sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
            		
            		 .requestMatchers("/api/admin/**").hasRole("ADMIN")

                .requestMatchers(
                        "/api/auth/**",
                        "/internal/**",
                      //  "/api/appointments/**" ,
                        "/api/boardings",
                        "/api/boardings/**",
                        
                        "/ws/**",

                        "/api/users/public/**",
                        
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html"
                ).permitAll()
                
                .requestMatchers("/api/payments/**").hasRole("STUDENT")

                .requestMatchers("/api/owner/**").hasRole("OWNER")
                .requestMatchers("/api/boardings/owner/**").hasRole("OWNER")

                .requestMatchers("/api/reports/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/reports/**").hasAnyRole("STUDENT", "OWNER")

                .requestMatchers("/api/student/**").hasRole("STUDENT")
                .requestMatchers("/api/bills/student/**").hasRole("STUDENT")
                


                .requestMatchers("/api/admin/**").hasRole("ADMIN")

                .requestMatchers("/api/owner/**").hasRole("OWNER")
                .requestMatchers("/api/boardings/owner/**").hasRole("OWNER")

                .requestMatchers("/api/student/**").hasRole("STUDENT")

                .anyRequest().authenticated()
            );

        return http.build();
    }
     
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
