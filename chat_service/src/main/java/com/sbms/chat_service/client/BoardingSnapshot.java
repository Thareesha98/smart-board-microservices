package com.sbms.chat_service.client;

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
