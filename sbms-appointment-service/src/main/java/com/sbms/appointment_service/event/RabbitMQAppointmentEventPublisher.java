package com.sbms.appointment_service.event;


import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Service
public class RabbitMQAppointmentEventPublisher
        implements AppointmentEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${sbms.rabbitmq.exchange:sbms.events}")
    private String exchangeName;

    public RabbitMQAppointmentEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publish(String eventType,
                        Long targetUserId,
                        Long appointmentId,
                        Map<String, Object> data) {

        try {
            EventMessage event = EventMessage.builder()
                    .eventType(eventType)
                    .sourceService("appointment-service")
                    .aggregateId(String.valueOf(appointmentId))
                    .userId(targetUserId != null ? String.valueOf(targetUserId) : null)
                    .data(data)
                    .occurredAt(Instant.now())
                    .build();

            // IMPORTANT: send OBJECT, not JSON string
            rabbitTemplate.convertAndSend(
                    exchangeName,
                    eventType,
                    event
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to publish appointment event", e);
        }
    }
}
