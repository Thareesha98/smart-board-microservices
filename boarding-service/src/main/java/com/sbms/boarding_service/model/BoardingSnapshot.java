package com.sbms.boarding_service.model;

import java.math.BigDecimal;

public record BoardingSnapshot(
        Long id,
        Long ownerId,
        String title,
        BigDecimal pricePerMonth,
        BigDecimal keyMoney,
        int availableSlots
) {}
