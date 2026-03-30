package com.sbms.appointment_service.event;



import java.util.Map;

public interface AppointmentEventPublisher {

    void publish(
            String eventType,
            Long targetUserId,
            Long appointmentId,
            Map<String, Object> data
    );
}
