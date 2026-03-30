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

        // -----------------------------
        // GET USER ID FROM GATEWAY HEADER
        // -----------------------------
        String userIdHeader = httpRequest.getHeader("X-User-Id");

        if (userIdHeader == null || userIdHeader.isBlank()) {
            throw new IllegalStateException("❌ Missing X-User-Id header from gateway");
        }

        // -----------------------------
        // CONVERT TO LONG (SAFE)
        // -----------------------------
        Long userId;
        try {
            userId = Long.parseLong(userIdHeader);
        } catch (NumberFormatException e) {
            throw new IllegalStateException("❌ Invalid X-User-Id format");
        }

        // -----------------------------
        // SESSION ID (IMPORTANT DESIGN)
        // -----------------------------
        // Use userId as sessionId OR combine with timestamp/device
        String sessionId = "USER_" + userId;

        // -----------------------------
        // CALL CHATBOT SERVICE
        // -----------------------------
        return chatbotService.chat(
                request.getMessage(),
                sessionId,
                userId
        );
    }
}