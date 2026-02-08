package com.sbms.sbms_payment_service.client;


import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.sbms.sbms_payment_service.dto.BoardingInfo;


@Service
public class BoardingClient {

    private final WebClient webClient;

    public BoardingClient(WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl("http://boarding-service:8080")
                .build();
    }

    public BoardingInfo getBoarding(Long boardingId) {
        return webClient.get()
                .uri("/api/boardings/internal/{id}/basic", boardingId)
                .retrieve()
                .bodyToMono(BoardingInfo.class)
                .timeout(java.time.Duration.ofSeconds(2))
                .block();
    }
}
