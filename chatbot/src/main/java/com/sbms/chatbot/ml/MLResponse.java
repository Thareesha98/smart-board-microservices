package com.sbms.chatbot.ml;

import lombok.Data;

@Data
public class MLResponse {
    private String intent;
    private double confidence;
}
