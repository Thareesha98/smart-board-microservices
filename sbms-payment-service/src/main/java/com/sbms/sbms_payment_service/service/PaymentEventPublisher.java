package com.sbms.sbms_payment_service.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.sbms.sbms_payment_service.entity.PaymentIntent;
import com.sbms.sbms_payment_service.entity.PaymentTransaction;
import com.sbms.sbms_payment_service.events.PaymentPendingApprovalEvent;
import com.sbms.sbms_payment_service.events.PaymentSucceededEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;



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