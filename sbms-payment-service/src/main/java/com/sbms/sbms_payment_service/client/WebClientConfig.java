package com.sbms.sbms_payment_service.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * GLOBAL WebClient configuration.
 * We expose ONLY the Builder to avoid bean conflicts in microservices.
 * Each client will set its own baseUrl (BEST PRACTICE in Kubernetes).
 */
@Configuration
public class WebClientConfig {

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}