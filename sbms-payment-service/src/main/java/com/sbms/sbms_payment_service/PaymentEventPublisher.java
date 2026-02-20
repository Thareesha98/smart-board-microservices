package com.sbms.sbms_payment_service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import com.sbms.sbms_payment_service.events.PaymentFailedEvent;
import com.sbms.sbms_payment_service.events.PaymentSucceededEvent;

import org.springframework.transaction.event.TransactionPhase;


import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPaymentSuccess(PaymentSucceededEvent event) {

        rabbitTemplate.convertAndSend(
                "payment.exchange",
                "payment.succeeded",
                event.getEventId()   // IDENTITY ONLY
        );
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPaymentFailed(PaymentFailedEvent event) {

        rabbitTemplate.convertAndSend(
                "payment.exchange",
                "payment.failed",
                event.paymentId()
        );
    }
}
