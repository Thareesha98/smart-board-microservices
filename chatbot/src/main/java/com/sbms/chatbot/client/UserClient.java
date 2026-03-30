package com.sbms.chatbot.client;




import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.sbms.chatbot.dto.client.UserMinimalDTO;

@Service
@RequiredArgsConstructor
public class UserClient {

    private final WebClient userWebClient;

    public UserMinimalDTO getByEmail(String email) {
        return userWebClient.get()
                .uri("/api/internal/users/by-email?email={email}", email)
                .retrieve()
                .bodyToMono(UserMinimalDTO.class)
                .block();
    }
    
    public UserMinimalDTO getById(Long userId) {
        return userWebClient.get()
                .uri("/api/internal/users/{id}/minimal", userId)
                .retrieve()
                .bodyToMono(UserMinimalDTO.class)
                .block();
    }
}