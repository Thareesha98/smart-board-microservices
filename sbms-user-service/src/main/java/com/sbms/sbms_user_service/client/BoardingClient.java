package com.sbms.sbms_user_service.client;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.sbms.sbms_user_service.dto.client.BoardingSummaryDTO;

@Service
public class BoardingClient {

    private final WebClient webClient;

    public BoardingClient(@org.springframework.beans.factory.annotation.Qualifier("boardingWebClient") WebClient boardingWebClient) {
        this.webClient = boardingWebClient;
    }

    public List<BoardingSummaryDTO> getOwnerBoardings(Long ownerId) {
        return webClient.get()
                .uri("/api/boarding/internal/owners/{id}/boardings", ownerId)
                .retrieve()
                .bodyToFlux(BoardingSummaryDTO.class)
                .collectList()
                .block();
    }
}