package com.sbms.chatbot.ml;


import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class MLClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String ML_URL = "http://localhost:8000/predict";

    public MLResponse predict(String text) {
        MLRequest request = new MLRequest(text);
        return restTemplate.postForObject(ML_URL, request, MLResponse.class);
    }

    @Data
    static class MLRequest {
        private final String text;
    }
}
