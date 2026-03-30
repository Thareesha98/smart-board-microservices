package com.sbms.chat_service.dto.chat;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MarkAsReadRequest {

    @NotNull
    private Long chatRoomId;
}
