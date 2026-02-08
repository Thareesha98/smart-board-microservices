package com.sbms.sbms_payment_service.dto;


public record GatewayInitiationResult(
        String gatewayReference,
        String redirectUrl
) {}
