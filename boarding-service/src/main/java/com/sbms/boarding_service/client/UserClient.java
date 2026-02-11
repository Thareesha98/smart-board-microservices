package com.sbms.boarding_service.client;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.sbms.boarding_service.dto.common.UserMinimalDTO;


@Service
public class UserClient {

    private final WebClient webClient;

    public UserClient(WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl("http://user-service:8080")
                .build();
    }

    public UserMinimalDTO getUserMinimal(Long userId) {
        return webClient.get()
                .uri("/api/user/internal/users/{id}/minimal", userId)
                .retrieve()
                .bodyToMono(UserMinimalDTO.class)
                .block(); // OK for internal sync calls
    }
}
