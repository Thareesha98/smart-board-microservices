package com.sbms.sbms_maintenance_service.client;


import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.RequiredArgsConstructor;





import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserClient {

   
	private final RestTemplate restTemplate;

    // Updated to match the TechnicianController RequestMapping
    private static final String BASE_URL = 
            "http://user-service:8080/api/technician-workflow";

    public void submitTechnicianReview(TechnicianReviewRequest dto) {
        // Updated path: /api/technician-workflow/reviews/internal
        restTemplate.postForObject(
                BASE_URL + "/reviews/internal",
                dto,
                Void.class
        );
    }

    // Inner DTO specifically for this internal transfer
    @Data
    public static class TechnicianReviewRequest {
        private Long ownerId;
        private Long technicianId;
        private Long maintenanceId;
        private int rating;
        private String comment;
    }
}