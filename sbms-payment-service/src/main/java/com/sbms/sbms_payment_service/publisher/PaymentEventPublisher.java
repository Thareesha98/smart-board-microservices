package com.sbms.sbms_payment_service.publisher;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.sbms.sbms_payment_service.events.PaymentPendingApprovalEvent;
import com.sbms.sbms_payment_service.events.PaymentSucceededEvent;






@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${sbms.rabbitmq.exchange:sbms.events}")
    private String exchange;

    public void publishPaymentSucceeded(PaymentSucceededEvent event) {
        log.info("Publishing PaymentSucceededEvent: intentId={}", event.getIntentId());
        rabbitTemplate.convertAndSend(exchange, "payment.succeeded", event);
    }

    public void publishPendingApproval(PaymentPendingApprovalEvent event) {
        log.info("Publishing PaymentPendingApprovalEvent: intentId={}", event.getIntentId());
        rabbitTemplate.convertAndSend(exchange, "payment.pending-approval", event);
    }
}