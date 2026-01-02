package com.sbms.sbms_backend.model;


import java.util.List;

import com.sbms.sbms_backend.common.BaseEntity;
import com.sbms.sbms_backend.model.enums.MaintenanceStatus;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "maintenance_requests")
public class Maintenance extends BaseEntity {
	
	@ManyToOne
    @JoinColumn(name = "registration_id", nullable = true)
    private Registration registration;

    @ManyToOne
    @JoinColumn(name = "boarding_id", nullable = false)
    private Boarding boarding;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student;  

    @Column(nullable = false, length = 150)
    private String title; 

    @Column(nullable = false, length = 1000)
    private String description;

    private List<String> imageUrls; // optional (S3 later)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MaintenanceStatus status = MaintenanceStatus.PENDING;

    private String studentNote;
    private String ownerNote;
}
