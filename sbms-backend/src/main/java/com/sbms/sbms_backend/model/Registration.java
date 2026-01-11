package com.sbms.sbms_backend.model;

import com.sbms.sbms_backend.common.BaseEntity;
import com.sbms.sbms_backend.model.enums.RegistrationStatus;
import com.sbms.sbms_backend.model.Registration;
import com.sbms.sbms_backend.model.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Registration extends BaseEntity{

	@Column(name = "boarding_id", nullable = false)
	private Long boardingId;
	
	@ManyToOne
	@JoinColumn(name="student_id", nullable=false)
	private User student;
	
	@Column(nullable= false)
	private int numberOfStudents;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
    private RegistrationStatus status = RegistrationStatus.PENDING;
	
	private boolean keyMoneyPaid = false;
	
	private String studentNote;
    private String ownerNote;
    
    @Column
    private String paymentTransactionRef;

    
    
    
	
}
