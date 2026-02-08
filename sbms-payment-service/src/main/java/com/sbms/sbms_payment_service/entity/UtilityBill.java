package com.sbms.sbms_payment_service.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(
    name = "utility_bills",
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"boardingId", "month"}
    )
)
@Getter
@Setter
public class UtilityBill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔑 IDs ONLY — no cross-service relations
    @Column(nullable = false)
    private Long boardingId;

    @Column(nullable = false)
    private Long ownerId;

    // YYYY-MM
    @Column(nullable = false, length = 7)
    private String month;

    @Column(nullable = false)
    private BigDecimal electricityAmount;

    @Column(nullable = false)
    private BigDecimal waterAmount;
}

