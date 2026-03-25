package com.sbms.appointment_service.dto;


import lombok.Data;

import java.time.LocalDateTime;

import com.sbms.appointment_service.domain.AppointmentStatus;

@Data
public class AppointmentOwnerDecisionDTO {

    // ACCEPTED or DECLINED
    private AppointmentStatus status;

    // Required only when ACCEPTED
    private LocalDateTime ownerStartTime;
    private LocalDateTime ownerEndTime;

    private String ownerNote;
}
