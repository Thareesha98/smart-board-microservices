package com.sbms.sbms_payment_service.client;


import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.sbms.sbms_payment_service.dto.user.UserMinimalDTO;

import java.time.Duration;



import reactor.core.publisher.Mono;

@Service
public class UserClient {

    private final WebClient webClient;

    public UserClient(WebClient userServiceWebClient) {
        this.webClient = userServiceWebClient;
    }

    public UserMinimalDTO findByEmail(String email) {
        return webClient.get()
                .uri("/api/internal/users/by-email?email={email}", email)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError(),
                        response -> Mono.error(new RuntimeException("User not found"))
                )
                .onStatus(
                        status -> status.is5xxServerError(),
                        response -> Mono.error(new RuntimeException("User service down"))
                )
                .bodyToMono(UserMinimalDTO.class)
                .timeout(Duration.ofSeconds(3))
                .block();
    }

    public UserMinimalDTO getUserMinimal(Long userId) {
        return webClient.get()
                .uri("/api/internal/users/{id}/minimal", userId)
                .retrieve()
                .bodyToMono(UserMinimalDTO.class)
                .block();
    }
}
