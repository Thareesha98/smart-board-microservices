package com.sbms.sbms_backend.client;

import java.time.Duration;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.sbms.sbms_backend.dto.user.UserMinimalDTO;
import com.sbms.sbms_backend.dto.user.UserSnapshotDTO;

import reactor.core.publisher.Mono;

@Service
public class UserClient {

    private final WebClient webClient;
    


    public UserClient(WebClient userServiceWebClient) {
        this.webClient = userServiceWebClient;
    }

      public boolean userExists(Long userId) {

        return webClient.get()
                .uri("/api/user/internal/users/exists/{id}", userId)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError(),
                        response -> Mono.error(new RuntimeException("User not found"))
                    )
                    .onStatus(
                        status -> status.is5xxServerError(),
                        response -> Mono.error(new RuntimeException("User service down"))
                    )
                .bodyToMono(Boolean.class)
                .timeout(Duration.ofSeconds(3))
                .block();
    }

     public UserMinimalDTO getUserMinimal(Long userId) {

        return webClient.get()
                .uri("/api/user/internal/users/{id}/minimal", userId)
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

     public UserSnapshotDTO getUserSnapshot(Long userId) {

        return webClient.get()
                .uri("/api/user/internal/users/{id}/snapshot", userId)
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
     
     
  // Add this to your UserClient.java
     public UserMinimalDTO findByEmail(String email) {
         return webClient.get()
                 .uri("/api/user/internal/users/by-email?email={email}", email)
                 .retrieve()
                 .bodyToMono(UserMinimalDTO.class)
                 .block();
     }
     
}
