package com.sbms.sbms_payment_service.events;


import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PaymentPendingApprovalEvent extends BaseEvent {

    private Long intentId;
    private Long studentId;
    private Long ownerId;
    private Long monthlyBillId;
    private String method;

    public PaymentPendingApprovalEvent() {
        super("payment.pending-approval");
    }
}