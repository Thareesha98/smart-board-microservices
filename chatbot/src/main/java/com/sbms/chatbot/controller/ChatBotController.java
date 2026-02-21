package com.sbms.chatbot.controller;

import com.sbms.chatbot.dto.ChatRequest;
import com.sbms.chatbot.dto.ChatResponse;
import com.sbms.chatbot.service.ChatbotService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chatbot")
@RequiredArgsConstructor
public class ChatBotController {

    private final ChatbotService chatbotService;

    @PostMapping("/chat")
    public ChatResponse chat(
            @RequestBody ChatRequest request,
            HttpServletRequest httpRequest
    ) {
        // TRUSTED HEADER FROM GATEWAY
        String userId = httpRequest.getHeader("X-User-Id");

        if (userId == null || userId.isBlank()) {
            throw new IllegalStateException("X-User-Id header missing");
        }

        return chatbotService.chat(
                request.getMessage(),
                userId   // sessionId = userId
        );
    }
}
