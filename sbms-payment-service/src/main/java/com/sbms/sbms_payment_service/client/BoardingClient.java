package com.sbms.sbms_payment_service.client;

import java.time.Duration;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.sbms.sbms_payment_service.entity.BoardingSnapshot;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class BoardingClient {

    private final WebClient webClient;

    public BoardingClient(WebClient.Builder builder) {
        this.webClient = builder
                // Kubernetes service DNS (correct)
                .baseUrl("http://boarding-service:8080")
                .build();
    }

    public BoardingSnapshot getBoarding(Long boardingId) {
        try {
            return webClient.get()
                    .uri("/api/boardings/internal/{boardingId}", boardingId)
                    .retrieve()
                    .bodyToMono(BoardingSnapshot.class)
                    .timeout(Duration.ofSeconds(3))
                    .block();
        } catch (Exception ex) {
            log.error("Failed to fetch boarding snapshot for id={}", boardingId, ex);
            return null; // graceful fallback for PDF generation
        }
    }
}
