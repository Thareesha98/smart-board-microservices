package com.sbms.appointment_service.domain;


import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "appointment_idempotency",
    uniqueConstraints = @UniqueConstraint(columnNames = "idempotencyKey")
)
public class AppointmentIdempotency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String idempotencyKey;

    @Column(nullable = false)
    private Long appointmentId;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected AppointmentIdempotency() {}

    public AppointmentIdempotency(String key, Long appointmentId) {
        this.idempotencyKey = key;
        this.appointmentId = appointmentId;
        this.createdAt = LocalDateTime.now();
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }
}
