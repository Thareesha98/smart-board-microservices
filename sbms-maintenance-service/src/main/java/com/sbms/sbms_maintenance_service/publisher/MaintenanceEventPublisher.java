package com.sbms.sbms_maintenance_service.publisher;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.sbms.sbms_maintenance_service.model.Maintenance;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MaintenanceEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void created(Maintenance m) {
        rabbitTemplate.convertAndSend(
                "maintenance.exchange",
                "maintenance.created",
                m.getId()
        );
    }

    public void updated(Maintenance m) {
        rabbitTemplate.convertAndSend(
                "maintenance.exchange",
                "maintenance.updated",
                m.getId()
        );
    }
}
