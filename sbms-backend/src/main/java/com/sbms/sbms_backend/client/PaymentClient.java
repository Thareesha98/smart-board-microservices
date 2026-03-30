package com.sbms.sbms_backend.client;


import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.sbms.sbms_backend.model.PaymentIntent;
import com.sbms.sbms_backend.model.enums.PaymentType;

@Component
public class PaymentClient {

    private static final String PAYMENT_SERVICE_BASE =
            "http://payment-service:8080/api/payments/internal";

    private final RestTemplate restTemplate;

    public PaymentClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

  
    public PaymentIntent getPaymentIntent(Long boardingId, Long studentId, PaymentType type) {
    	
    	
    	String url = PAYMENT_SERVICE_BASE +
                "/getIntent/{boardingId}/{studentId}/{paymentType}";

        return restTemplate.getForObject(
                url,
                PaymentIntent.class,
                boardingId,
                studentId,
                type
        );
    	
    	
    }

  
   

    
  

}
  