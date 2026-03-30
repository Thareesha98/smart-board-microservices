package com.sbms.chat_service.dto.chat;


import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class ChatMessageResponse {

    private Long id;

    private Long chatRoomId;

    private Long senderId;

    private String senderRole; // STUDENT / OWNER

    private String content;

    private boolean read;

    private Instant createdAt;
}
