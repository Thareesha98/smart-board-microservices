package com.sbms.sbms_user_service.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

	@Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public WebClient reportWebClient(WebClient.Builder builder) {
        // Match the service name and port in your K8s deployment
        return builder.baseUrl("http://report-service:8083").build();
    }

    @Bean
    public WebClient boardingWebClient(WebClient.Builder builder) {
        // Match the service name and port in your K8s deployment
        return builder.baseUrl("http://boarding-service:8080").build();
    }
    
    @Bean(name = "maintenanceWebClient")
    public WebClient maintenanceWebClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder
                .baseUrl("http://maintenance-service:8080")
                .build();
    }
}