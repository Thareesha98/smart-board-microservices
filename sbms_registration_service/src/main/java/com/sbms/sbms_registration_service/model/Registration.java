package com.sbms.sbms_registration_service.model;

import com.sbms.sbms_registration_service.common.BaseEntity;
import com.sbms.sbms_registration_service.enums.RegistrationStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "registrations")
@Data
public class Registration extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long boardingId;

    @Column(nullable = false)
    private Long studentId;

    @Column(nullable = false)
    private Long ownerId;

    @Column(nullable = false)
    private int numberOfStudents;

    @Enumerated(EnumType.STRING)
    private RegistrationStatus status = RegistrationStatus.PENDING;

    private boolean keyMoneyPaid;

    private String studentNote;
    private String ownerNote;

    private String paymentTransactionRef;
}
