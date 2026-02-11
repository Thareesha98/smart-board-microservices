package com.sbms.sbms_maintenance_service.publisher;

import com.sbms.sbms_maintenance_service.client.BoardingClient;
import com.sbms.sbms_maintenance_service.model.Maintenance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class MaintenanceEventPublisher {

    private static final String EXCHANGE = "sbms.events";

    private final RabbitTemplate rabbitTemplate;
    private final BoardingClient boardingClient;

    
    
    
    public void created(Maintenance m) {

        Long ownerId = boardingClient.getBoardingOwner(m.getBoardingId()).ownerId();

        EventMessageDto event = new EventMessageDto(
                "maintenance.requested",
                String.valueOf(ownerId), // ✅ OWNER gets notification
                Map.of(
                        "maintenanceId", m.getId(),
                        "boardingId", m.getBoardingId(),
                        "title", m.getTitle(),
                        "studentId", m.getStudentId()
                )
        );

        rabbitTemplate.convertAndSend(
                "sbms.events",
                "maintenance.requested",
                event
        );

        log.info("📣 Maintenance requested → notified OWNER {}", ownerId);
    }
    
    
    
    
    
    
    
    
    
    
    
    

    public void updated(Maintenance m) {

        if (m == null || m.getId() == null || m.getStudentId() == null) {
            log.warn("⚠️ Skipping maintenance.updated event due to null data: {}", m);
            return;
        }

        EventMessageDto event = new EventMessageDto(
                "maintenance.updated",
                String.valueOf(m.getStudentId()),
                Map.of(
                        "maintenanceId", m.getId(),
                        "status", m.getStatus().name()
                )
        );

        rabbitTemplate.convertAndSend(
                EXCHANGE,
                "maintenance.updated",
                event
        );

        log.info("📤 Published maintenance.updated → studentId={}, maintenanceId={}, status={}",
                m.getStudentId(), m.getId(), m.getStatus());
    }
}
