package com.sbms.boarding_service.event;

import java.time.Instant;
import java.util.Map;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQBoardingEventPublisher
        implements BoardingEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${sbms.rabbitmq.exchange:sbms.events}")
    private String exchangeName;

    public RabbitMQBoardingEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publish(String eventType,
                        Long targetUserId,
                        Long boardingId,
                        Map<String, Object> data) {

        try {
            EventMessage event = EventMessage.builder()
                    .eventType(eventType)
                    .sourceService("boarding-service")
                    .aggregateId(String.valueOf(boardingId))
                    .userId(targetUserId != null ? String.valueOf(targetUserId) : null)
                    .data(data)
                    .occurredAt(Instant.now())
                    .build();

            rabbitTemplate.convertAndSend(
                    exchangeName,
                    eventType,
                    event
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to publish boarding event", e);
        }
    }
}
