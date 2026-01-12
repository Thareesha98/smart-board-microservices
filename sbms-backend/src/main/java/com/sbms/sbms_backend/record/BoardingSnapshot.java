package com.sbms.sbms_backend.record;

import java.math.BigDecimal;

public record BoardingSnapshot(
        Long id,
        String title,
        String address,
        String ownerName,
        BigDecimal pricePerMonth,
        BigDecimal keyMoney,
        int availableSlots,
        Long ownerId
) {}
