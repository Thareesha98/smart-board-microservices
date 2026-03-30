package com.sbms.chat_service.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(
        name="chat_messages",
        indexes = {
                @Index(name="idx_chat_message_room_time",columnList = "chat_room_id,created_at"),
                @Index(name="idx_chat_message_unread",columnList = "chat_room_id,is_read")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long chatRoomId;

    private Long senderId;

    @Enumerated(EnumType.STRING)
    private SenderRole senderRole;

    @Column(columnDefinition = "TEXT")
    private String content;

    private boolean read;

    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = Instant.now();
    }

    public enum SenderRole {
        STUDENT,
        OWNER
    }
}