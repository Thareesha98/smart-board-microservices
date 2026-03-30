package com.sbms.appointment_service.event;


import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
public class EventMessage {

    private String eventType;        // e.g. appointment.created
    private String sourceService;    // appointment-service
    private String aggregateId;      // appointmentId
    private String userId;           // receiver userId (string)
    private Map<String, Object> data;
    private Instant occurredAt;
}
