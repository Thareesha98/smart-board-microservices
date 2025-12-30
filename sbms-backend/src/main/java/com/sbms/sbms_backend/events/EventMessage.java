package com.sbms.sbms_backend.events;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
public class EventMessage {

    private String eventType;        // e.g. "appointment.created"
    private String sourceService;    // e.g. "sbms-backend"
    private String aggregateId;      // e.g. appointmentId, registrationId
    private String userId;           // target user (string)
    private Map<String, Object> data;
    private Instant occurredAt;
}
