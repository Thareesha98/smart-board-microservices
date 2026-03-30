package com.sbms.sbms_user_service.client;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.sbms.sbms_user_service.dto.client.ReportResponseDTO;

@Service
public class ReportClient {

    private final WebClient webClient;

    // Use Qualifier to pick the 'reportWebClient' bean
    public ReportClient(@org.springframework.beans.factory.annotation.Qualifier("reportWebClient") WebClient reportWebClient) {
        this.webClient = reportWebClient;
    }

    public List<ReportResponseDTO> getReportsAgainstUser(Long userId) {
        return webClient.get()
                .uri("/api/report/internal/user/{id}", userId) // Ensure full internal path
                .retrieve()
                .bodyToFlux(ReportResponseDTO.class)
                .collectList()
                .block();
    }
}