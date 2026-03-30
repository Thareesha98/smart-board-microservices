package com.sbms.chat_service.client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.sbms.chat_service.dto.user.UserMinimalDTO;
import com.sbms.chat_service.enums.UserRole;

@Service
public class UserClient {

    private final RestClient restClient;
    private static final Logger log = LoggerFactory.getLogger(UserClient.class);

    public UserClient(RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("http://user-service:8080") // K8s DNS
                .build();
    }

    public UserMinimalDTO getUserMinimal(Long userId) {
        try {
            return restClient.get()
                    .uri("/api/internal/users/{id}/minimal", userId)
                    .retrieve()
                    .body(UserMinimalDTO.class);
        } catch (Exception ex) {
            log.error("User Service minimal fetch FAILED for id={}", userId, ex);
            return new UserMinimalDTO(
                    userId, 
                    "Unknown User", 
                    "", 
                    "OWNER",
                    false    
            );
        }
    }
}