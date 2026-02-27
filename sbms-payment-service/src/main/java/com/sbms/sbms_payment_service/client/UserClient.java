package com.sbms.sbms_payment_service.client;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.sbms.sbms_payment_service.dto.user.UserMinimalDTO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserClient {

    private final WebClient webClient;

    public UserClient(WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl("http://user-service:8080") // K8s DNS (correct)
                .build();
    }

    /**
     * SIMPLE + STABLE (Same pattern as boarding-service)
     * Remove timeout + reactor operators + resilience conflicts
     */
    public UserMinimalDTO findByEmail(String email) {

        if (email == null || email.isBlank()) {
            log.error("X-User-Email header is NULL or empty");
            return null;
        }

        log.info("Calling User Service (sync) for email={}", email);

        try {
            UserMinimalDTO user = webClient.get()
                    .uri(uriBuilder ->
                            uriBuilder
                                    .path("/api/internal/users/by-email")
                                    .queryParam("email", email)
                                    .build()
                    )
                    .retrieve()
                    .bodyToMono(UserMinimalDTO.class)
                    .block(); // 🔥 SAME AS YOUR WORKING BOARDING CLIENT

            log.info("User Service SUCCESS -> {}", user);
            return user;

        } catch (Exception ex) {
            log.error("User Service call FAILED for email={}", email, ex);
            return null; // graceful fallback for payment flow
        }
    }

    public UserMinimalDTO getUserMinimal(Long userId) {
        try {
            return webClient.get()
                    .uri("/api/internal/users/{id}/minimal", userId)
                    .retrieve()
                    .bodyToMono(UserMinimalDTO.class)
                    .block();
        } catch (Exception ex) {
            log.error("User Service minimal fetch FAILED for id={}", userId, ex);
            return null;
        }
    }
}