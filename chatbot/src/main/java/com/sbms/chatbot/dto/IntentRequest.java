package com.sbms.chatbot.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IntentRequest {
    private String text;
}
