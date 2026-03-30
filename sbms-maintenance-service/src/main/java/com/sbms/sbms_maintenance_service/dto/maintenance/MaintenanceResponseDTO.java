package com.sbms.sbms_maintenance_service.dto.maintenance;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.sbms.sbms_maintenance_service.model.enums.MaintenanceIssueType;
import com.sbms.sbms_maintenance_service.model.enums.MaintenanceStatus;
import com.sbms.sbms_maintenance_service.model.enums.MaintenanceUrgency;

import lombok.Data;

@Data
public class MaintenanceResponseDTO {

    private Long id;

    private Long boardingId;
    private String boardingTitle;
    private Long studentId;
    private String studentName;

    private String title;
    private String description;
    private List<String> imageUrls;

    private MaintenanceStatus status;

    private String studentNote;
    private String ownerNote;

    private Long registrationId;
    
    private MaintenanceIssueType maintnanceIssueType; 
    private MaintenanceUrgency maintenanceUrgency; 
    
    private Long assignedTechnicianId;
    private boolean rejectedByTechnician;
    private String technicianRejectionReason;
    private BigDecimal technicianFee;
    
    private Integer ownerRating;
    private String ownerComment;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    private String boardingAddress;
    private String ownerName;
    private String ownerPhone;
    private String technicianName;
    private String reviewComment;
}
