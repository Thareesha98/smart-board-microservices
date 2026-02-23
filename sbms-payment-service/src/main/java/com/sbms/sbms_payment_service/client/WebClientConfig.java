package com.sbms.sbms_payment_service.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient userServiceWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl("http://user-service:8080")
                .build();
    }

    @Bean
    public WebClient emailServiceWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl("http://user-service:8080")
                .build();
    }

    @Bean
    public WebClient boardingServiceWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl("http://boarding-service:8080")
                .build();
    }

    @Bean
    public WebClient registrationServiceWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl("http://registration-service:8080")
                .build();
    }

    @Bean
    public WebClient fileServiceWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl("http://sbms-backend:8080")
                .build();
    }
}