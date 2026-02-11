package com.sbms.sbms_backend.client;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient userServiceWebClient() {
        return WebClient.builder()
                .baseUrl("http://user-service:8080")
                .build();
    }
}
