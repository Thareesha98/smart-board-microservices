package com.sbms.sbms_maintenance_service.dto.maintenance;

import java.time.LocalDateTime;

import com.sbms.sbms_maintenance_service.model.enums.MaintenanceStatus;

import lombok.Data;

@Data
public class MaintenanceDecisionDTO {

    private MaintenanceStatus status; // IN_PROGRESS / COMPLETED / REJECTED
    private String ownerNote;
    
    
    
    private LocalDateTime updatedAt;
}
