package com.sbms.chatbot.service;

import com.sbms.chatbot.model.UnknownIntentLog;
import com.sbms.chatbot.repository.UnknownIntentLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class UnknownIntentLogger {

    private final UnknownIntentLogRepository repository;

    public void log(String sessionId, String message, double confidence) {

        UnknownIntentLog log = new UnknownIntentLog();
        log.setSessionId(sessionId);
        log.setMessage(message);
        log.setConfidence(confidence);
        log.setTimestamp(Instant.now());

        repository.save(log);
    }
}
