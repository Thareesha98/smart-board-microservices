package com.sbms.sbms_payment_service.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(
    name = "owner_wallet_transactions",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_owner_wallet_reference",
            columnNames = {"ownerId", "reference"}
        )
    }
)
@Getter
@Setter
public class OwnerWalletTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long ownerId;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 100)
    private String reference; // paymentId

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = OffsetDateTime.now();
    }
}
