package com.sbms.sbms_backend.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.sbms.sbms_backend.model.enums.ManualApprovalStatus;
import com.sbms.sbms_backend.model.enums.PaymentIntentStatus;
import com.sbms.sbms_backend.model.enums.PaymentMethod;
import com.sbms.sbms_backend.model.enums.PaymentType;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "payment_intents")
@Getter
@Setter
public class PaymentIntent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long studentId;
    private Long ownerId;
    private Long boardingId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentType type;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentIntentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ManualApprovalStatus manualApprovalStatus;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime completedAt;
    private LocalDateTime expiresAt;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(nullable = false, unique = true)
    private String referenceId;

    @Column(nullable=true ,   name = "monthly_bill_id")
    private Long monthlyBillId;


    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private PaymentMethod method;

    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}

