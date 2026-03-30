package com.sbms.sbms_payment_service.publisher;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.sbms.sbms_payment_service.dto.EventMessageDto;
import com.sbms.sbms_payment_service.events.PaymentPendingApprovalEvent;
import com.sbms.sbms_payment_service.events.PaymentSucceededEvent;
import com.sbms.sbms_payment_service.service.PaymentRollbackEvent;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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

    // =========================
    // PAYMENT SUCCESS EVENT
    // =========================
    public void publishPaymentSucceeded(PaymentSucceededEvent event) {

        rabbitTemplate.convertAndSend(
                exchange,
                "payment.succeeded",
                event
        );

        Map<String, Object> data = new HashMap<>();
        data.put("intentId", event.getIntentId());
        data.put("transactionId", event.getTransactionId());
        data.put("amount", event.getAmount());
        data.put("method", event.getMethod());
        data.put("monthlyBillId", event.getMonthlyBillId());
        data.put("transactionRef", event.getTransactionRef());

        EventMessageDto notificationEvent = new EventMessageDto(
                "payment.succeeded",          // eventType
                "payment-service",            // sourceService
                String.valueOf(event.getIntentId()), // aggregateId
                String.valueOf(event.getStudentId()), // userId (IMPORTANT)
                data,
                Instant.now()
        );

        rabbitTemplate.convertAndSend(
                exchange,
                "payment.succeeded.notification",
                notificationEvent
        );

        log.info("Published PaymentSucceeded + Notification event for intent={}", event.getIntentId());
    }

    // =========================
    // PENDING APPROVAL EVENT (Cash/Bank Slip)
    // =========================
    public void publishPendingApproval(PaymentPendingApprovalEvent event) {

        rabbitTemplate.convertAndSend(
                exchange,
                "payment.pending-approval",
                event
        );

        Map<String, Object> data = new HashMap<>();
        data.put("intentId", event.getIntentId());
        data.put("method", event.getMethod());
        data.put("monthlyBillId", event.getMonthlyBillId());

        EventMessageDto notificationEvent = new EventMessageDto(
                "payment.pending_approval",
                "payment-service",
                String.valueOf(event.getIntentId()),
                String.valueOf(event.getStudentId()), // notify student
                data,
                Instant.now()
        );

        rabbitTemplate.convertAndSend(
                exchange,
                "payment.pending.notification",
                notificationEvent
        );

        log.info("Published PaymentPendingApproval notification event for intent={}", event.getIntentId());
    }
    
    
    
    
    
    
    
    
    public void publishRollbackRequested(PaymentRollbackEvent event) {

        rabbitTemplate.convertAndSend(
                exchange,
                "payment.rollback.requested",
                event
        );

        // 🔹 Also notify user (important UX)
        Map<String, Object> data = new HashMap<>();
        data.put("intentId", event.getIntentId());
        data.put("transactionId", event.getTransactionId());
        data.put("reason", event.getReason());

        EventMessageDto notificationEvent = new EventMessageDto(
                "payment.failed",
                "payment-service",
                String.valueOf(event.getIntentId()),
                null, // optional (you can attach userId if needed)
                data,
                Instant.now()
        );

        rabbitTemplate.convertAndSend(
                exchange,
                "payment.failed.notification",
                notificationEvent
        );

        log.error(" Published PAYMENT_ROLLBACK_REQUESTED intent={} reason={}",
                event.getIntentId(),
                event.getReason());
    }
}