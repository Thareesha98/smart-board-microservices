package com.sbms.sbms_payment_service.events;


import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public abstract class BaseEvent implements Serializable {

    private String eventId;
    private String eventType;
    private LocalDateTime occurredAt;

    protected BaseEvent(String eventType) {
        this.eventId = UUID.randomUUID().toString();
        this.eventType = eventType;
        this.occurredAt = LocalDateTime.now();
    }
}