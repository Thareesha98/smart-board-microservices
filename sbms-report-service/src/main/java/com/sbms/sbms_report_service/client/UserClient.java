package com.sbms.sbms_report_service.client;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.sbms.sbms_report_service.model.dto.UserMinimalDTO;

@Service
public class UserClient {
    private final WebClient webClient;

    public UserClient(WebClient.Builder webClientBuilder) {
        // Ensure port matches your K8s Service (likely 8081 based on previous logs)
        this.webClient = webClientBuilder.baseUrl("http://user-service:8080").build();
    }

    public UserMinimalDTO getUserMinimal(Long userId) {
        if (userId == null) return null;
        try {
            return webClient.get()
                    .uri("/api/user/internal/users/{id}/minimal", userId)
                    .retrieve()
                    .bodyToMono(UserMinimalDTO.class)
                    .block();
        } catch (Exception e) {
            return null; // Fallback if user service is down
        }
    }
}