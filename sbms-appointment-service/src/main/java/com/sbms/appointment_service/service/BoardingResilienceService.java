package com.sbms.appointment_service.service;

import com.sbms.appointment_service.client.BoardingClient;
import com.sbms.appointment_service.dto.BoardingOwnerInfo;
import com.sbms.appointment_service.exception.BoardingServiceUnavailableException;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.springframework.stereotype.Service;

@Service
public class BoardingResilienceService {

    private final BoardingClient boardingClient;

    public BoardingResilienceService(BoardingClient boardingClient) {
        this.boardingClient = boardingClient;
    }

    @CircuitBreaker(
            name = "appointment-boarding-cb",
            fallbackMethod = "boardingFallback"
    )
    @Retry(name = "appointment-boarding-retry")
    public BoardingOwnerInfo getBoardingOwner(Long boardingId) {
        return boardingClient.getBoardingOwner(boardingId);
    }


    public BoardingOwnerInfo boardingFallback(Long boardingId, Throwable ex) {
        throw new BoardingServiceUnavailableException(
                "Cannot create appointment. Boarding service unavailable."
        );
    }


}
