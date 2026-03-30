package com.sbms.appointment_service.dto;


import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.sbms.appointment_service.domain.AppointmentStatus;

@Data
public class AppointmentResponseDTO {

    private Long id;

    // ---- References ----
    private Long studentId;
    private Long ownerId;
    private Long boardingId;
    private String boardingAddress;
    private String distance;
    
    

    private String studentName;
    private String studentEmail;

    private String ownerName;
    private String ownerContact;
   
    private String boardingTitle;

    // ---- Appointment details ----
    private int numberOfStudents;

    private LocalDateTime requestedStartTime;
    private LocalDateTime requestedEndTime;

    private LocalDateTime ownerStartTime;
    private LocalDateTime ownerEndTime;

    private AppointmentStatus status;

    private String studentNote;
    private String ownerNote;

    // ---- Audit (optional, but useful) ----
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    private String boardingImage;


}
