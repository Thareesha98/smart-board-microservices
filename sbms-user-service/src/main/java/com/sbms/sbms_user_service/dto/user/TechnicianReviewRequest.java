package com.sbms.sbms_user_service.dto.user;

import lombok.Data;

@Data
public class TechnicianReviewRequest {
    private Long ownerId;
    private Long technicianId;
    private Long maintenanceId;
    private int rating;
    private String comment;
}