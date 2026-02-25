package com.sbms.sbms_registration_service.dto;


import lombok.Builder;

@Builder
public record BoardingOwnerInfo(
        Long ownerId,
        String boardingTitle
) {}
