package com.sbms.sbms_payment_service.client;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RegistrationClient {

    private final WebClient webClient;

    // 🔥 FIX: Use @Qualifier to resolve bean conflict
    public RegistrationClient(
            @Qualifier("registrationServiceWebClient") WebClient webClient
    ) {
        this.webClient = webClient;
    }

    @CircuitBreaker(name = "registrationService", fallbackMethod = "fallbackRegistrations")
    @Retry(name = "registrationService")
    public List<ApprovedRegistrationDTO> getApprovedRegistrations(Long boardingId) {

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
        return List.of(); // graceful fallback (VERY IMPORTANT)
    }

    public record ApprovedRegistrationDTO(
            Long studentId,
            BigDecimal monthlyRent
    ) {}
}