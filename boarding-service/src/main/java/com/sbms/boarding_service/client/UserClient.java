package com.sbms.boarding_service.client;

import java.time.Duration;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.sbms.boarding_service.dto.boarding.UserSnapshotDTO;
import com.sbms.boarding_service.dto.common.UserMinimalDTO;

import reactor.core.publisher.Mono;


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
    
    
    public UserSnapshotDTO getUserSnapshot(Long userId) {

        return webClient.get()
                .uri("/api/internal/users/{id}/snapshot", userId)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError(),
                        response -> Mono.error(new RuntimeException("User not found"))
                    )
                    .onStatus(
                        status -> status.is5xxServerError(),
                        response -> Mono.error(new RuntimeException("User service down"))
                    )
                .bodyToMono(UserSnapshotDTO.class)
                .timeout(Duration.ofSeconds(3))
                .block();
    }
    
    
    
    
}
