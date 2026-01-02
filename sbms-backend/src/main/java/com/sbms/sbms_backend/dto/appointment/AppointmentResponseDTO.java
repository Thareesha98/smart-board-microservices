package com.sbms.sbms_backend.dto.appointment;

import com.sbms.sbms_backend.model.enums.AppointmentStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppointmentResponseDTO {

    private Long id;

    private Long boardingId;
    private String boardingTitle;
    private String boardingAddress;

    private Long studentId;
    private String studentName;
    private String studentEmail;

    private int numberOfStudents;

    private LocalDateTime requestedStartTime;
    private LocalDateTime requestedEndTime;

    private LocalDateTime ownerStartTime;
    private LocalDateTime ownerEndTime;

    private AppointmentStatus status;

    private String studentNote;
    private String ownerNote;
}
