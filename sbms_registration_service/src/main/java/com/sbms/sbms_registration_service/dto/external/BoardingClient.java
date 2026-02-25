package com.sbms.sbms_registration_service.dto.external;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.sbms.sbms_registration_service.dto.BoardingOwnerInfo;

@Service
public class BoardingClient {

    private final WebClient webClient;

    public BoardingClient(WebClient.Builder builder) {
        this.webClient = builder
               // .baseUrl("http://sbms-backend:8080")
        		.baseUrl("http://boarding-service:8080")

                .build();
    }

    public BoardingOwnerInfo getBoardingOwner(Long boardingId) {
        return webClient.get()
        		.uri("/api/boardings/internal/{id}/owner", boardingId)
                .retrieve()
                .bodyToMono(BoardingOwnerInfo.class)
                .timeout(java.time.Duration.ofSeconds(2))
                .block();
    }
    
    public BoardingSnapshot getSnapshot(Long boardingId) {
    	return webClient.get()
    			.uri("/api/boardings/internal/{boardingId}", boardingId)
    			.retrieve()
    			.bodyToMono(BoardingSnapshot.class)
    			.timeout(java.time.Duration.ofSeconds(2))
    			.block();
    }
}

