package com.sbms.chat_service.client;

import com.sbms.chat_service.dto.client.BoardingFullSnapshot;
import com.sbms.chat_service.dto.client.BoardingOwnerInfo;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class BoardingClient {

    private final RestClient restClient;

    private static final String BASE_URL = "http://boarding-service:8080/api/boardings";

    public BoardingClient(RestClient.Builder builder) {
        this.restClient = builder.baseUrl(BASE_URL).build();
    }

    public BoardingSnapshot getBoarding(Long boardingId) {
        return restClient.get()
                .uri("/internal/{id}", boardingId)
                .retrieve()
                .body(BoardingSnapshot.class);
    }

    public BoardingOwnerInfo getOwnerInfo(Long boardingId) {
        return restClient.get()
                .uri("/internal/{id}/owner", boardingId)
                .retrieve()
                .body(BoardingOwnerInfo.class);
    }

    public Long getOwnerId(Long boardingId) {
        BoardingOwnerInfo info = getOwnerInfo(boardingId);
        return info != null ? info.ownerId() : null;
    }

    public List<Long> getBoardingIdsByOwner(Long ownerId) {
        return restClient.get()
                .uri("/internal/owner/{id}/ids", ownerId)
                .retrieve()
                .body(List.class);
    }

    public void reserveSlots(Long boardingId, int count) {
        restClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/internal/{id}/reserve-slots")
                        .queryParam("count", count)
                        .build(boardingId))
                .retrieve()
                .toBodilessEntity();
    }

    public BoardingFullSnapshot getBoardingFull(Long boardingId) {
        return restClient.get()
                .uri("/internal/{id}/full", boardingId)
                .retrieve()
                .body(BoardingFullSnapshot.class);
    }
}