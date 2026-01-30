package com.sbms.sbms_maintenance_service.model.enums;


public enum MaintenanceStatus {
    PENDING,        // submitted by student
    IN_PROGRESS,    // accepted by owner
    COMPLETED,      // fixed
    REJECTED        // rejected by owner
}
