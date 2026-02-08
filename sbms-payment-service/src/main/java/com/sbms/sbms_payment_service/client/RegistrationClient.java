package com.sbms.sbms_payment_service.client;


import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RegistrationClient {

    private final WebClient registrationWebClient;

    /**
     * Returns approved registrations for a boarding
     */
    public List<ApprovedRegistrationDTO> getApprovedRegistrations(
            Long boardingId
    ) {
        return registrationWebClient
                .get()
                .uri("/internal/registrations/approved/{boardingId}", boardingId)
                .retrieve()
                .bodyToFlux(ApprovedRegistrationDTO.class)
                .collectList()
                .block();
    }

    public record ApprovedRegistrationDTO(
            Long studentId,
            BigDecimal monthlyRent
    ) {}
}

