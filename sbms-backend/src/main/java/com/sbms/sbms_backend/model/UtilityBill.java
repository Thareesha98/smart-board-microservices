package com.sbms.sbms_backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.YearMonth;

import com.sbms.sbms_backend.common.BaseEntity;

@Data
@Entity
@Table(
    name = "utility_bills",
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"boarding_id", "bill_month"}
    )
)
public class UtilityBill extends BaseEntity {

    @Column(name = "boarding_id", nullable = false)
    private Long boardingId;

    // YYYY-MM (e.g. 2025-01)
    @Column(name = "bill_month", nullable = false, length = 7)
    private String month;

    @Column(nullable = false)
    private BigDecimal electricityAmount;

    @Column(nullable = false)
    private BigDecimal waterAmount;
}
