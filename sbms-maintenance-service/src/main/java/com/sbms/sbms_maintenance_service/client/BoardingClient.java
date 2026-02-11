package com.sbms.sbms_maintenance_service.client;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.sbms.sbms_maintenance_service.dto.maintenance.BoardingOwnerInfo;

import lombok.RequiredArgsConstructor;




@Component
@RequiredArgsConstructor
public class BoardingClient {

    private final RestTemplate restTemplate;

    private static final String BASE =
            "http://boarding-service:8080/api/boardings/internal";

    // -------------------------------------------------
    // Validate boarding exists
    // -------------------------------------------------
    public void validateBoarding(Long boardingId) {
        restTemplate.exchange(
                BASE + "/" + boardingId,
                HttpMethod.GET,
                null,
                Void.class
        );
    }

    // -------------------------------------------------
    // Get owner info (returns BoardingOwnerInfo)
    // -------------------------------------------------
    public BoardingOwnerInfo getBoardingOwner(Long boardingId) {
        return restTemplate.getForObject(
                BASE + "/" + boardingId + "/owner",
                BoardingOwnerInfo.class
        );
    }

    // -------------------------------------------------
    // Get boarding IDs by owner ✅ FIXED
    // -------------------------------------------------
    public List<Long> getBoardingIdsByOwner(Long ownerId) {
        return restTemplate.exchange(
                BASE + "/owner/" + ownerId + "/ids",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Long>>() {}
        ).getBody();
    }
}
