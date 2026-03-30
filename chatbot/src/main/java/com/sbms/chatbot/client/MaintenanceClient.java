package com.sbms.chatbot.client;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.sbms.chatbot.dto.client.MaintenanceResponseDTO;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MaintenanceClient {

    private final WebClient maintenanceWebClient;

    public List<MaintenanceResponseDTO> getMyMaintenance(Long userId) {
        return maintenanceWebClient.get()
                .uri("/api/maintenance/student")
                .header("X-User-Id", userId.toString())
                .retrieve()
                .bodyToFlux(MaintenanceResponseDTO.class)
                .collectList()
                .block();
    }
}