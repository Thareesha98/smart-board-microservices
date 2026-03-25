package com.sbms.appointment_service.client;

import java.time.Duration;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.sbms.appointment_service.dto.UserSnapshotDTO;

import reactor.core.publisher.Mono;


@Service
public class UserClient {

    private final WebClient webClient;

    public UserClient(WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl("http://user-service:8080")
                .build();
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
