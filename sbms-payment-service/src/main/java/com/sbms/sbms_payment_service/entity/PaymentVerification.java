package com.sbms.sbms_payment_service.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(
    name = "payment_verifications",
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"paymentId"}
    )
)
@Getter
@Setter
public class PaymentVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long paymentId;

    @Column(nullable = false)
    private String method; // BANK_TRANSFER or CASH

    // For slip upload
    private String slipUrl;

    // Owner action
    private Boolean approved;
    private Long reviewedByOwnerId;
    private OffsetDateTime reviewedAt;

    private OffsetDateTime createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = OffsetDateTime.now();
    }
}
