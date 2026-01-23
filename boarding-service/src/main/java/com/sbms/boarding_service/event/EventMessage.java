package com.sbms.boarding_service.event;

import java.time.Instant;
import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EventMessage {

    private String eventType;        // boarding.created
    private String sourceService;    // boarding-service
    private String aggregateId;      // boardingId
    private String userId;           // receiver userId
    private Map<String, Object> data;
    private Instant occurredAt;
}
