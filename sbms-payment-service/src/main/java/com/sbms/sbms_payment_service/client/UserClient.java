package com.sbms.sbms_payment_service.client;


import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.sbms.sbms_payment_service.dto.UserBasicInfo;


@Service
public class UserClient {

    private final WebClient webClient;

    public UserClient(WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl("http://user-service:8080")
                .build();
    }

    public UserBasicInfo getUser(Long userId) {
        return webClient.get()
                .uri("/api/users/internal/{id}/basic", userId)
                .retrieve()
                .bodyToMono(UserBasicInfo.class)
                .timeout(java.time.Duration.ofSeconds(2))
                .block();
    }
}
