package com.sbms.sbms_maintenance_service.model;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;

import com.sbms.maintenance_service.common.BaseEntity;
import com.sbms.sbms_maintenance_service.model.enums.MaintenanceStatus;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.JoinColumn;
import lombok.Data;

@Entity
@Table(name = "maintenances")
@Data
public class Maintenance extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long boardingId;

    @Column(nullable = false)
    private Long studentId;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, length = 1000)
    private String description;

    @ElementCollection
    @CollectionTable(
        name = "maintenance_images",
        joinColumns = @JoinColumn(name = "maintenance_id")
    )
    @Column(name = "image_url")
    private List<String> imageUrls;

    @Enumerated(EnumType.STRING)
    private MaintenanceStatus status = MaintenanceStatus.PENDING;

    private String studentNote;
    private String ownerNote;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    
}
