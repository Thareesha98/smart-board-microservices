package com.sbms.sbms_payment_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient registrationWebClient(
            @Value("${services.registration.base-url}") String baseUrl
    ) {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }
}
