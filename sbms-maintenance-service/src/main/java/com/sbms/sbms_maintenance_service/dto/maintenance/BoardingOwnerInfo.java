package com.sbms.sbms_maintenance_service.dto.maintenance;
import lombok.Builder;

@Builder
public record BoardingOwnerInfo(
        Long ownerId,
        String boardingTitle
) {}
