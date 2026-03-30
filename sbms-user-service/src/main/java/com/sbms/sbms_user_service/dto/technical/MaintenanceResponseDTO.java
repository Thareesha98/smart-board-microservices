package com.sbms.sbms_user_service.dto.technical;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.sbms.sbms_user_service.enums.MaintenanceStatus;
import com.sbms.sbms_user_service.enums.MaintenanceUrgency;

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
    
    
    
     private BigDecimal technicianFee;
    private MaintenanceUrgency maintenanceUrgency;


    private Long ownerId;
    private String ownerPhone;

    private int ownerRating;
    private String ownerComment;

    private Double latitude;
    private Double longitude;

    private String technicianName;
    private Long technicianId;
    private String ownerName;
    private String boardingAddress;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Integer rating;
    private String reviewComment;
}
