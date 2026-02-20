package com.sbms.sbms_payment_service.entity;

import java.math.BigDecimal;

public record BoardingSnapshot(
	    Long id,
	    Long ownerId,
	    String title,
	    BigDecimal pricePerMonth,
	    BigDecimal keyMoney,
	    int availableSlots
	) {}
