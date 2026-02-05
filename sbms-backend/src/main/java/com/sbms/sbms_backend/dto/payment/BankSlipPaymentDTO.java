package com.sbms.sbms_backend.dto.payment;

import org.springframework.web.multipart.MultipartFile;

public record BankSlipPaymentDTO(
        Long intentId,
        MultipartFile slipImage
) {}
