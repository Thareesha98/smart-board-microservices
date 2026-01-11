package com.sbms.sbms_backend.model;


import com.sbms.sbms_backend.common.BaseEntity;
import com.sbms.sbms_backend.model.enums.PaymentMethod;
import com.sbms.sbms_backend.model.enums.PaymentStatus;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "payment_transactions")
public class PaymentTransaction extends BaseEntity {

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private PaymentMethod method;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Column(unique = true)
    private String transactionRef;

    private String failureReason;
    
    @Column(length = 500)
    private String receiptUrl;

}
