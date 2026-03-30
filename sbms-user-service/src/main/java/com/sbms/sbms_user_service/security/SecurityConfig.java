package com.sbms.sbms_user_service.security;


import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;
    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(
            JwtAuthenticationFilter jwtFilter,
            CustomUserDetailsService userDetailsService
    ) {
        this.jwtFilter = jwtFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm ->
                    sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth

                // PUBLIC
                .requestMatchers(
                        "/api/auth/**",
                        "/actuator/**",
                        "/v3/api-docs/**",
                        "/swagger-ui/**"
                ).permitAll()
                
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                
                .requestMatchers("/api/internal/**").permitAll()
                
             // Inside SecurityConfig.java
                .requestMatchers("/api/technician-workflow/**")
                    .hasAnyAuthority("TECHNICIAN", "OWNER", "STUDENT", "ROLE_TECHNICIAN", "ROLE_OWNER", "ROLE_STUDENT")


                // ADMIN
                .requestMatchers("/api/users/admin/**")
                    .hasRole("ADMIN")
                    
                    .requestMatchers("/api/owner/**")
                    .hasRole("OWNER")
                    
                    .requestMatchers("/api/student/**")
                    .hasRole("STUDENT")

                // AUTHENTICATED
                .requestMatchers("/api/users/**").permitAll()

                .anyRequest().authenticated()
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(
                    jwtFilter,
                    UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
    

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }



    @Bean
    public AuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);
        
        provider.setPasswordEncoder(passwordEncoder());

     

        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(authenticationProvider());
    }
    
    
    
   
}
