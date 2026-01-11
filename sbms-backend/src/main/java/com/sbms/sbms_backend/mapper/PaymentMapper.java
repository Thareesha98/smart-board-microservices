package com.sbms.sbms_backend.mapper;


import com.sbms.sbms_backend.dto.payment.PaymentHistoryDTO;
import com.sbms.sbms_backend.model.PaymentTransaction;

public class PaymentMapper {

    public static PaymentHistoryDTO toDTO(PaymentTransaction tx) {
        PaymentHistoryDTO dto = new PaymentHistoryDTO();
        dto.setId(tx.getId());
        dto.setTransactionRef(tx.getTransactionRef());
        dto.setAmount(tx.getAmount());
        dto.setMethod(tx.getMethod());
        dto.setStatus(tx.getStatus());
        dto.setFailureReason(tx.getFailureReason());
        dto.setPaidAt(tx.getCreatedAt());
        dto.setReceiptUrl(tx.getReceiptUrl());

        return dto;
    }
}
