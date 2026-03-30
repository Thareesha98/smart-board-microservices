package com.sbms.sbms_payment_service.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sbms.sbms_notification_service.model.enums.PaymentType;
import com.sbms.sbms_payment_service.entity.PaymentIntent;
import com.sbms.sbms_payment_service.repository.PaymentIntentRepository;

@RestController
@RequestMapping("/api/payments/internal")
public class PaymentInternalController {
	
	private final PaymentIntentRepository paymentIntentRepo;

	
	 public PaymentInternalController(PaymentIntentRepository paymentIntentRepo) {
	        this.paymentIntentRepo = paymentIntentRepo;
	    }
	
	@GetMapping("/getIntent/{boardingId}/{studentId}/{paymentType}")
	public PaymentIntent getIntent(
			@PathVariable  Long studentId,
			@PathVariable  Long boardingId,
			@PathVariable PaymentType paymentType
			
			) {
		
		PaymentIntent intent = paymentIntentRepo
                .findTopByStudentIdAndBoardingIdAndTypeOrderByCreatedAtDesc(
                        studentId,
                        boardingId,
                        paymentType
                )
                .orElseThrow(() -> new RuntimeException("Key money payment required"));
		
		return intent;
		
	}

	
}
