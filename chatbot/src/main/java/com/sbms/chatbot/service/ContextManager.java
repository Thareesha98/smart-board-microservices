/*package com.sbms.chatbot.service;


import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ContextManager {

    private final Map<String, ChatContext> contextStore = new ConcurrentHashMap<>();

    public void updateContext(String sessionId, String intent) {
        contextStore.put(sessionId, new ChatContext(intent, Instant.now()));
    }

    public ChatContext getContext(String sessionId) {
        return contextStore.get(sessionId);
    }

    public void clearContext(String sessionId) {
        contextStore.remove(sessionId);
    }

    public record ChatContext(String lastIntent, Instant timestamp) {}
}  */




package com.sbms.chatbot.service;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ContextManager {

    // 5-minute session timeout
    private static final Duration CONTEXT_TIMEOUT = Duration.ofMinutes(5);

    private final Map<String, ChatContext> contextStore = new ConcurrentHashMap<>();

    public void updateContext(String sessionId, String intent) {
        contextStore.put(sessionId, new ChatContext(intent, Instant.now()));
    }

    public ChatContext getContext(String sessionId) {

        ChatContext context = contextStore.get(sessionId);

        if (context == null) {
            return null;
        }

        // Check expiry
        if (isExpired(context)) {
            contextStore.remove(sessionId);
            return null;
        }

        return context;
    }

    private boolean isExpired(ChatContext context) {
        return Duration.between(context.timestamp(), Instant.now())
                .compareTo(CONTEXT_TIMEOUT) > 0;
    }

    // Optional manual clear
    public void clearContext(String sessionId) {
        contextStore.remove(sessionId);
    }

    // Context record
    public record ChatContext(String lastIntent, Instant timestamp) {}
}


