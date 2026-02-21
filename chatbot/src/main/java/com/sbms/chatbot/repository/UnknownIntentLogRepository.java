package com.sbms.chatbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sbms.chatbot.model.UnknownIntentLog;

public interface UnknownIntentLogRepository
        extends JpaRepository<UnknownIntentLog, Long> {
}
