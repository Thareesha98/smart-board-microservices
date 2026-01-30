package com.sbms.sbms_maintenance_service.client;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BoardingClient {

    private final RestTemplate restTemplate;

    private static final String BASE =
            "http://boarding-service/api/boardings/internal";

    public void validateBoarding(Long boardingId) {
        restTemplate.getForObject(BASE + "/" + boardingId, Void.class);
    }

    public Long getBoardingOwner(Long boardingId) {
        return restTemplate.getForObject(
                BASE + "/" + boardingId + "/owner",
                Long.class);
    }

    public List<Long> getBoardingIdsByOwner(Long ownerId) {
        return restTemplate.exchange(
                BASE + "/owner/" + ownerId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Long>>() {}
        ).getBody();
    }
}
