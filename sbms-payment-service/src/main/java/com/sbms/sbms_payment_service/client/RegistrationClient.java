package com.sbms.sbms_payment_service.client;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RegistrationClient {

    private final WebClient webClient;

    public RegistrationClient(WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl("http://registration-service:8080")
                .build();
    }

    @CircuitBreaker(name = "registrationService", fallbackMethod = "fallbackRegistrations")
    @Retry(name = "registrationService")
    public List<ApprovedRegistrationDTO> getApprovedRegistrations(Long boardingId) {

        log.info("Calling Registration Service for boardingId={}", boardingId);

        return webClient.get()
                .uri("/internal/registrations/approved/{boardingId}", boardingId)
                .retrieve()
                .bodyToFlux(ApprovedRegistrationDTO.class)
                .collectList()
                .timeout(Duration.ofSeconds(3))
                .block();
    }

    public List<ApprovedRegistrationDTO> fallbackRegistrations(Long boardingId, Throwable ex) {
        log.error("Registration service DOWN for boardingId={}", boardingId, ex);
        return List.of();
    }

    public record ApprovedRegistrationDTO(
            Long studentId,
            BigDecimal monthlyRent
    ) {}
}