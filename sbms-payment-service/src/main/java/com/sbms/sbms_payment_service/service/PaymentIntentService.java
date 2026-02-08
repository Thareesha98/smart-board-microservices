package com.sbms.sbms_payment_service.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.sbms.sbms_payment_service.entity.PaymentIntent;
import com.sbms.sbms_payment_service.entity.enums.PaymentIntentStatus;
import com.sbms.sbms_payment_service.repository.PaymentIntentRepository;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class PaymentIntentService {

    private final PaymentIntentRepository repository;

    @Transactional
    public PaymentIntent markProcessing(PaymentIntent intent) {

        if (intent.getStatus() != PaymentIntentStatus.CREATED) {
            throw new IllegalStateException(
                    "PaymentIntent not in CREATED state");
        }

        intent.setStatus(PaymentIntentStatus.PROCESSING);
        return repository.save(intent);
    }

    @Transactional
    public void markSuccess(PaymentIntent intent) {

        if (intent.getStatus() == PaymentIntentStatus.SUCCESS) {
            return; // idempotent
        }

        intent.setStatus(PaymentIntentStatus.SUCCESS);
        intent.setCompletedAt(OffsetDateTime.now());
        repository.save(intent);
    }

    @Transactional
    public void markFailed(PaymentIntent intent, String reason) {

        if (intent.getStatus() == PaymentIntentStatus.SUCCESS) {
            return; // cannot rollback success
        }

        intent.setStatus(PaymentIntentStatus.FAILED);
        repository.save(intent);
    }
}
