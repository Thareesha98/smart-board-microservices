package com.sbms.chatbot.config;



import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient userWebClient() {
        return WebClient.builder()
                .baseUrl("http://user-service:8080")
                .build();
    }

    @Bean
    public WebClient billingWebClient() {
        return WebClient.builder()
                .baseUrl("http://sbms-backend:8080")
                .build();
    }

    @Bean
    public WebClient maintenanceWebClient() {
        return WebClient.builder()
                .baseUrl("http://maintenance-service:8080")
                .build();
    }
    
    
    @Bean
    public WebClient appointmentWebClient() {
        return WebClient.builder()
                .baseUrl("http://appointment-service:8080")
                .build();
    }
}