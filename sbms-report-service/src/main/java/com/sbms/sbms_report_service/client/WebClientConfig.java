package com.sbms.sbms_report_service.client;



import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    // 1. Define the Builder bean
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    // 2. Define the WebClient bean (optional if you only use the builder in Clients)
    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder.build();
    }
}