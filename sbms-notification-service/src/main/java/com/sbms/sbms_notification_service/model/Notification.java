package com.sbms.sbms_notification_service.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "notifications", indexes = {
        @Index(name = "idx_user_read", columnList = "user_id, read_flag")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @Column(name = "notification_id", nullable = false)
    private String notificationId; // use UUID string

    @Column(name = "user_id", nullable = false)
    private String userId;

    private String title;

    @Column(length = 4000)
    private String message;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "read_flag")
    private boolean read;

    // optional: payload JSON
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "meta", columnDefinition = "jsonb")
    private String meta;

}