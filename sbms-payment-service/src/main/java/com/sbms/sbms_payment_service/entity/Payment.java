package com.sbms.sbms_payment_service.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.sbms.sbms_payment_service.entity.enums.PaymentStatus;

@Entity
@Table(
    name = "payments",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_payment_gateway_ref",
            columnNames = {"gateway", "gatewayReference"}
        ),
        @UniqueConstraint(
            name = "uk_payment_idempotency",
            columnNames = {"idempotencyKey"}
        )
    },
    indexes = {
        @Index(name = "idx_payment_intent", columnList = "paymentIntentId")
    }
)
@Getter
@Setter
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ----------- RELATION -----------

    @Column(nullable = false)
    private Long paymentIntentId;

    // ----------- IDEMPOTENCY -----------

    @Column(nullable = false, length = 100, updatable = false)
    private String idempotencyKey;

    // ----------- GATEWAY -----------

    @Column(nullable = false, length = 50)
    private String gateway;

    @Column(length = 100)
    private String gatewayReference;

    // ----------- MONEY -----------

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    // ----------- STATE -----------

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status;

    // ----------- FAILURE INFO -----------

    @Column(length = 255)
    private String failureReason;

    // ----------- TIMESTAMPS -----------

    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    private OffsetDateTime completedAt;

    @PrePersist
    void onCreate() {
        this.createdAt = OffsetDateTime.now();
    }
}
