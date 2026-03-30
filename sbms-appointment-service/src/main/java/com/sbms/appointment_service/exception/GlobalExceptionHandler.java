package com.sbms.appointment_service.exception;

import java.util.Map;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.HttpStatus;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BoardingServiceUnavailableException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public Map<String, String> handleBoardingUnavailable(
            BoardingServiceUnavailableException ex
    ) {
        return Map.of(
                "error", "BOARDING_SERVICE_UNAVAILABLE",
                "message", ex.getMessage()
        );
    }
}
