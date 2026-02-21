package com.sbms.chatbot.dto;

import lombok.Data;

@Data
public class IntentResponse {
    private String intent;
    private double confidence;
    private boolean fallback;
}
