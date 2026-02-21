package com.sbms.sbms_payment_service.client;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class RegistrationClient {

    private final WebClient registrationWebClient;

    @CircuitBreaker(name = "registrationService", fallbackMethod = "fallbackRegistrations")
    public List<ApprovedRegistrationDTO> getApprovedRegistrations(Long boardingId) {
        return registrationWebClient
                .get()
                .uri("/internal/registrations/approved/{boardingId}", boardingId)
                .retrieve()
                .bodyToFlux(ApprovedRegistrationDTO.class)
                .collectList()
                .block();
    }

    public List<ApprovedRegistrationDTO> fallbackRegistrations(Long boardingId, Throwable ex) {
        log.error("Registration service DOWN for boardingId={}", boardingId, ex);
        return Collections.emptyList();
    }

    public record ApprovedRegistrationDTO(
            Long studentId,
            BigDecimal monthlyRent
    ) {}
}