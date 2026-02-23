package com.sbms.sbms_payment_service.service;
import com.sbms.sbms_notification_service.model.enums.ManualApprovalStatus;
import com.sbms.sbms_notification_service.model.enums.PaymentIntentStatus;
import com.sbms.sbms_notification_service.model.enums.PaymentType;
import com.sbms.sbms_payment_service.dto.CreatePaymentIntentDTO;
import com.sbms.sbms_payment_service.entity.PaymentIntent;
import com.sbms.sbms_payment_service.repository.PaymentIntentRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentIntentService {

    private final PaymentIntentRepository intentRepo;
    
    private String generateReferenceId() {
        return "PI-" + System.currentTimeMillis();
    }

  public PaymentIntent create(CreatePaymentIntentDTO dto) {

        PaymentIntent intent = new PaymentIntent();

        intent.setStudentId(dto.getStudentId());
        intent.setOwnerId(dto.getOwnerId());
        intent.setBoardingId(dto.getBoardingId());
        intent.setType(dto.getType());
        intent.setAmount(dto.getAmount());
        intent.setDescription(dto.getDescription());
        intent.setMonthlyBillId(dto.getMonthlyBillId());

        intent.setStatus(PaymentIntentStatus.CREATED);

        intent.setManualApprovalStatus(
            dto.getType() == PaymentType.KEY_MONEY
                ? ManualApprovalStatus.PENDING
                : ManualApprovalStatus.NOT_REQUIRED
        );
        intent.setExpiresAt(calculateExpiry(dto));


        intent.setCurrency("LKR");
        intent.setReferenceId(UUID.randomUUID().toString());

      //  intent.setReferenceType("KEY_MONEY");

        return intentRepo.save(intent);
    }



    private LocalDateTime calculateExpiry(CreatePaymentIntentDTO dto) {

        return switch (dto.getType()) {
            case KEY_MONEY -> LocalDateTime.now().plusHours(24);
            case MONTHLY_RENT, UTILITIES -> LocalDateTime.now().plusDays(14);
        };
    }


    private void validate(CreatePaymentIntentDTO dto) {

        if (dto.getStudentId() == null)
            throw new RuntimeException("Student ID required");

        if (dto.getOwnerId() == null)
            throw new RuntimeException("Owner ID required");

        if (dto.getBoardingId() == null)
            throw new RuntimeException("Boarding ID required");

        if (dto.getType() == null)
            throw new RuntimeException("Payment type required");

        if (dto.getAmount() == null || dto.getAmount().signum() <= 0)
            throw new RuntimeException("Invalid amount");

        if (dto.getType() != PaymentType.KEY_MONEY && dto.getMonthlyBillId() == null)
            throw new RuntimeException("Monthly bill ID required");

        if (dto.getType() == PaymentType.KEY_MONEY && dto.getMonthlyBillId() != null)
            throw new RuntimeException("Key money cannot reference monthly bill");
    }
}
