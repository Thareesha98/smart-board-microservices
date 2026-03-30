package com.sbms.chat_service.entity;


import lombok.*;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageSentEvent {

    private Long messageId;

    private Long chatRoomId;

    private Long senderId;

    private Long receiverId;

    private String content;

    private Instant createdAt;
}