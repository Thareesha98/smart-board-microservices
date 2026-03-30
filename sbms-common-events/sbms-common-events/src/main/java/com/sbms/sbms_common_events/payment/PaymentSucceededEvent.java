package com.sbms.sbms_common_events.payment;




import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentSucceededEvent {

    private String eventId;          // UUID for idempotency
    private Long intentId;
    private Long transactionId;

    private Long studentId;
    private Long ownerId;
    private Long boardingId;
    private Long monthlyBillId;

    private BigDecimal amount;
    private String currency;
    private String method;

    private String receiptPath; // optional (can be null initially)

    private LocalDateTime occurredAt;
}
