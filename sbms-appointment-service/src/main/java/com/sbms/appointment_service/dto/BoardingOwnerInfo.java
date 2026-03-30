package com.sbms.appointment_service.dto;
import lombok.Builder;

@Builder
public record BoardingOwnerInfo(
        Long ownerId,
        String boardingTitle
) {}
