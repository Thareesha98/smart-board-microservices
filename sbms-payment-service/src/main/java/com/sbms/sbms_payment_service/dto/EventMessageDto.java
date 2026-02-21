package com.sbms.sbms_payment_service.dto;


import java.time.Instant;
import java.util.Map;

public record EventMessageDto(
        String eventType,
        String sourceService,
        String aggregateId,
        String userId,
        Map<String, Object> data,
        Instant occurredAt
) {}