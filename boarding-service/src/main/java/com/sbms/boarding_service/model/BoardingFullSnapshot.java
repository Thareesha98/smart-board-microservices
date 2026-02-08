package com.sbms.boarding_service.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record BoardingFullSnapshot(
        Long id,
        Long ownerId,
        String title,
        String address,
        BigDecimal pricePerMonth,
        BigDecimal keyMoney,
        int availableSlots,
        List<String> imageUrls,
        LocalDateTime createdAt
) {}