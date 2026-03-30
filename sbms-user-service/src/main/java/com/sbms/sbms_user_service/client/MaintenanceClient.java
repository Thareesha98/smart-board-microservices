package com.sbms.sbms_user_service.client;




import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.sbms.sbms_user_service.dto.technical.MaintenanceResponseDTO;

@Service
public class MaintenanceClient {

    private final WebClient webClient;

    public MaintenanceClient(@org.springframework.beans.factory.annotation.Qualifier("maintenanceWebClient") WebClient maintenanceWebClient) {
        this.webClient = maintenanceWebClient;
    }

    public List<MaintenanceResponseDTO> getJobs(Long technicianId) {
        return webClient.get()
                // FIX: Change /api/maintenance/technician to /api/technician-workflow
                .uri("/api/technician-workflow") 
                .header("X-User-Id", String.valueOf(technicianId))
                .retrieve()
                .bodyToFlux(MaintenanceResponseDTO.class)
                .collectList()
                .block();
    }
}