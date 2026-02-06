package com.sbms.boarding_service.dto.common;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Schema(description = "Standard API error response")
public class ErrorResponse {

    @Schema(description = "HTTP status code", example = "404")
    private int status;

    @Schema(description = "Short error code for frontend handling", example = "BOARDING_NOT_FOUND")
    private String errorCode;

    @Schema(description = "Human readable error message", example = "Boarding not found")
    private String message;

    @Schema(description = "Time when error occurred", example = "2026-01-23T10:15:30")
    private LocalDateTime timestamp;
}
