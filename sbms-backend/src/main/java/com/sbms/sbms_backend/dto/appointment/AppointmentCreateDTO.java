package com.sbms.sbms_backend.dto.appointment;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppointmentCreateDTO {

    private Long boardingId;

    private int numberOfStudents;

    private LocalDateTime requestedStartTime;
    private LocalDateTime requestedEndTime;

    private String studentNote;
}
