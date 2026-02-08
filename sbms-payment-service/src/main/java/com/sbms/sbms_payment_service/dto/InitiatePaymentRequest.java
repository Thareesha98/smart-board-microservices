package com.sbms.sbms_payment_service.dto;

import jakarta.validation.constraints.NotBlank;

public record InitiatePaymentRequest(
        @NotBlank String gateway
) {}
