package com.sbms.sbms_backend.events;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NotificationEvent {

    private Long receiverId;       // user receiving notification
    private String title;
    private String message;
    private String type;           // INFO / ACTION / SUCCESS / WARNING
    private LocalDateTime createdAt = LocalDateTime.now();
}
