package com.sbms.sbms_user_service.model;


import jakarta.persistence.*;
import lombok.Data;

import com.sbms.sbms_user_service.common.BaseEntity;

@Data
@Entity
@Table(name = "technician_reviews")
public class TechnicianReview extends BaseEntity {

    private Long ownerId;

    private Long technicianId;

    private Long maintenanceId;

    private int rating;

    private String comment;
}