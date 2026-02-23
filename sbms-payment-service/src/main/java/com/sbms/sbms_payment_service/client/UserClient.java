package com.sbms.sbms_payment_service.client;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.sbms.sbms_payment_service.dto.user.UserMinimalDTO;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class UserClient {

    private final WebClient webClient;

    public UserClient(@Qualifier("userServiceWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "fallbackUser")
    @Retry(name = "userService")
    @TimeLimiter(name = "userService")
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
                        response -> Mono.error(new RuntimeException("User service unavailable"))
                )
                .bodyToMono(UserMinimalDTO.class)
                .timeout(Duration.ofSeconds(3))
                .block();
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "fallbackUserById")
    public UserMinimalDTO getUserMinimal(Long userId) {
        return webClient.get()
                .uri("/api/internal/users/{id}/minimal", userId)
                .retrieve()
                .bodyToMono(UserMinimalDTO.class)
                .timeout(Duration.ofSeconds(3))
                .block();
    }

    // 🔥 FALLBACK METHODS (VERY IMPORTANT)
    public UserMinimalDTO fallbackUser(String email, Throwable ex) {
        log.error("User service DOWN for email={}", email, ex);
        return null; // graceful degradation
    }

    public UserMinimalDTO fallbackUserById(Long userId, Throwable ex) {
        log.error("User service DOWN for id={}", userId, ex);
        return null;
    }
}