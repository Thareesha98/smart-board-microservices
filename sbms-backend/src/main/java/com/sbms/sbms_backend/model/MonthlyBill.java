package com.sbms.sbms_backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.sbms.sbms_backend.common.BaseEntity;
import com.sbms.sbms_backend.model.enums.MonthlyBillStatus;

@Data
@Entity
@Table(
    name = "monthly_bills",
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"student_id", "boarding_id", "bill_month"}
    )
)
public class MonthlyBill extends BaseEntity {

	@Column(nullable = false)
    private Long studentId;

    @Column(nullable = false)
    private Long boardingId;

    // YYYY-MM
    @Column(name = "bill_month", nullable = false, length = 7)
    private String month;

    @Column(nullable = false)
    private BigDecimal boardingFee;

    @Column(nullable = false)
    private BigDecimal electricityFee;

    @Column(nullable = false)
    private BigDecimal waterFee;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MonthlyBillStatus status = MonthlyBillStatus.UNPAID;
    
    @Column(nullable = false)
    private LocalDate dueDate;
    
    private LocalDateTime paidAt;
    
    @Column(name = "monthly_bill_id" ,  nullable = true)
    private Long monthlyBillId;


}
