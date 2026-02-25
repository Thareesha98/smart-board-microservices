package com.sbms.sbms_registration_service.dto.event;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegistrationEventDTO {

    private String eventType;

    private Long registrationId;
    private Long boardingId;
    private Long studentId;
    private Long ownerId;
}
