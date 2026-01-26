package com.sbms.sbms_notification_service.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "notification_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;     // stored as string for safety

    @Column(length = 500)
    private String expoToken;
}
