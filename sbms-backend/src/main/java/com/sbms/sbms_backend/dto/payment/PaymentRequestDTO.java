package com.sbms.sbms_backend.dto.payment;


import com.sbms.sbms_backend.model.enums.PaymentMethod;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequestDTO {

    private Long userId;

    private Long monthlyBillId;

    private BigDecimal amount;

    private PaymentMethod paymentMethod;
}
