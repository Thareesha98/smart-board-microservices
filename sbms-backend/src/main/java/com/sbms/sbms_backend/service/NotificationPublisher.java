package com.sbms.sbms_backend.service;

import com.sbms.sbms_backend.events.EventMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Service
public class NotificationPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${sbms.rabbitmq.exchange:sbms.events}")
    private String exchangeName;

    public NotificationPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(String eventType,
                        Long targetUserId,
                        String aggregateId,
                        Map<String, Object> data) {

        try {
            EventMessage event = EventMessage.builder()
                    .eventType(eventType)
                    .sourceService("sbms-backend")
                    .aggregateId(aggregateId)
                    .userId(targetUserId != null ? String.valueOf(targetUserId) : null)
                    .data(data)
                    .occurredAt(Instant.now())
                    .build();

            // IMPORTANT: send the object itself (not a JSON string).
            rabbitTemplate.convertAndSend(exchangeName, eventType, event);

        } catch (Exception e) {
            throw new RuntimeException("Failed to publish event", e);
        }
    }
}
