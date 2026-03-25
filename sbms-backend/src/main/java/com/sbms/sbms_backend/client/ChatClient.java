package com.sbms.sbms_backend.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.sbms.sbms_backend.events.EmergencyEventRequest;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ChatClient {

    private static final String CHAT_SERVICE_BASE =
            "http://chat-service:8080/api/emergency";

    private final RestTemplate restTemplate;

    public ChatClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // ----------------------------------------------------
    // SEND EMERGENCY EVENT TO CHAT SERVICE
    // ----------------------------------------------------
    public void sendEmergency(EmergencyEventRequest request) {

        restTemplate.postForLocation(
                CHAT_SERVICE_BASE + "/push",
                request
        );
    }
}