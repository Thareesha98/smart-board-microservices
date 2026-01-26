package com.sbms.sbms_notification_service.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class ExpoPushService {

    private final NotificationTokenService tokenService;
    private final WebClient webClient;

    public ExpoPushService(NotificationTokenService tokenService) {
        this.tokenService = tokenService;
        this.webClient = WebClient.builder()
                .baseUrl("https://exp.host/--/api/v2")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public void sendToUser(String userId, String title, String body) {
        String expoToken = tokenService.getTokenForUser(userId);

        if (expoToken == null) {
            System.out.println("⚠️ No Expo token for userId " + userId);
            return;
        }

        System.out.println("📨 Sending Expo push to userId=" + userId);

        // 🔥 IMPORTANT: Expo expects ARRAY, not single object
        List<Map<String, Object>> payload = List.of(
                Map.of(
                        "to", expoToken,
                        "sound", "default",
                        "title", title,
                        "body", body
                )
        );

        webClient.post()
                .uri("/push/send")
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(resp -> System.out.println("✅ Expo response: " + resp))
                .doOnError(err -> System.err.println("❌ Expo push error: " + err.getMessage()))
                .subscribe();
    }
}
