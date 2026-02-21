package com.sbms.chatbot.service;


import com.sbms.chatbot.dto.IntentRequest;
import com.sbms.chatbot.dto.IntentResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class IntentClient {

    private final RestTemplate restTemplate;

    @Value("${ml.intent.base-url}")
    private String mlBaseUrl;

    public IntentResponse predictIntent(String message) {
        IntentRequest request = new IntentRequest(message);

        return restTemplate.postForObject(
                mlBaseUrl + "/predict",
                request,
                IntentResponse.class
        );
    }
}
