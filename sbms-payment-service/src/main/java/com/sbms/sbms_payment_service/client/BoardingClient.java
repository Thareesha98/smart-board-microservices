package com.sbms.sbms_payment_service.client;

import java.time.Duration;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.sbms.sbms_payment_service.entity.BoardingSnapshot;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class BoardingClient {

    private final WebClient webClient;

    public BoardingClient(WebClient boardingServiceWebClient) {
        this.webClient = boardingServiceWebClient;
    }

    @CircuitBreaker(name = "boardingService", fallbackMethod = "fallbackBoarding")
    @TimeLimiter(name = "boardingService")
    public BoardingSnapshot getBoarding(Long boardingId) {
        return webClient.get()
                .uri("/api/boardings/internal/{boardingId}", boardingId)
                .retrieve()
                .bodyToMono(BoardingSnapshot.class)
                .timeout(Duration.ofSeconds(3))
                .block();
    }

    //  FALLBACK (PDF MUST NOT CRASH PAYMENT)
    public BoardingSnapshot fallbackBoarding(Long boardingId, Throwable ex) {
        log.error("Boarding service DOWN. Using fallback for boardingId={}", boardingId, ex);
        return new BoardingSnapshot(
                boardingId,
                null,
                "Boarding Info Unavailable",
                null,
                null,
                0
        );
    }
}