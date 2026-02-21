package com.sbms.chatbot.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Table(name = "unknown_intent_logs")
@Data
public class UnknownIntentLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sessionId;

    @Column(length = 500)
    private String message;

    private double confidence;

    private Instant timestamp;
}
