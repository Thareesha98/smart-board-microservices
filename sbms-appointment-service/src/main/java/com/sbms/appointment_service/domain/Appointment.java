package com.sbms.appointment_service.domain;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "appointments")
public class Appointment extends BaseEntity {

    // ---- External references (IDs only) ----
    @Column(nullable = false)
    private Long studentId;

    @Column(nullable = false)
    private Long ownerId;

    @Column(nullable = false)
    private Long boardingId;

    // ---- Appointment details ----
    @Column(nullable = false)
    private int numberOfStudents;

    @Column(nullable = false)
    private LocalDateTime requestedStartTime;

    @Column(nullable = false)
    private LocalDateTime requestedEndTime;

    private LocalDateTime ownerStartTime;
    private LocalDateTime ownerEndTime;

    // ---- Status & notes ----
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status = AppointmentStatus.PENDING;

    private String studentNote;
    private String ownerNote;
}
