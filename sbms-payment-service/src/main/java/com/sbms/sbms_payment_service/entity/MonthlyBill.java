package com.sbms.sbms_payment_service.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.sbms.sbms_payment_service.entity.enums.MonthlyBillStatus;

@Entity
@Table(
    name = "monthly_bills",
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"studentId", "boardingId", "month"}
    )
)
@Getter
@Setter
public class MonthlyBill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long studentId;
    private Long boardingId;
    private Long ownerId;

    @Column(nullable = false, length = 7)
    private String month; // YYYY-MM

    @Column(nullable = false)
    private BigDecimal boardingFee;

    @Column(nullable = false)
    private BigDecimal electricityFee;

    @Column(nullable = false)
    private BigDecimal waterFee;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    private MonthlyBillStatus status;

    private LocalDate dueDate;
}
