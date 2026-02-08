package com.sbms.sbms_payment_service.entity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.sbms.sbms_payment_service.entity.enums.PaymentIntentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;

@Entity
@Table(
		name="payment_intents",
		indexes= {
				@Index(name="idx_payment_intent_student" , columnList = "studentId"),
				@Index(name= "idx_payment_intent_reference" , columnList = "referenceType,referenceId" )
		}
)
@Getter
@Setter
public class PaymentIntent {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long studentId;
	
	@Column(nullable = false)
	private Long ownerId;
	
	 @Column(nullable = false)
	    private Long boardingId;

	    // ----------- BUSINESS CONTEXT -----------

	    @Column(nullable = false, length = 50)
	    private String referenceType; // MONTHLY_BILL, UTILITIES, KEY_MONEY

	    @Column(nullable = false, length = 100)
	    private String referenceId;

	    // ----------- MONEY -----------

	    @Column(nullable = false, precision = 12, scale = 2)
	    private BigDecimal amount;

	    @Column(nullable = false, length = 3)
	    private String currency;

	    // ----------- STATE -----------

	    @Enumerated(EnumType.STRING)
	    @Column(nullable = false, length = 20)
	    private PaymentIntentStatus status;

	    // ----------- TIMESTAMPS -----------

	    @Column(nullable = false, updatable = false)
	    private OffsetDateTime createdAt;

	    @Column(nullable = false)
	    private OffsetDateTime expiresAt;

	    private OffsetDateTime completedAt;

	    @PrePersist
	    void onCreate() {
	        this.createdAt = OffsetDateTime.now();
	    }
	}

