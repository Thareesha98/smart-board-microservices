package com.sbms.sbms_registration_service.dto.external;




import java.math.BigDecimal;

public record BoardingSnapshot(
        Long id,
        String title,
        Long ownerId,
        int availableSlots,
        BigDecimal keyMoney,
        BigDecimal pricePerMonth
) {}
