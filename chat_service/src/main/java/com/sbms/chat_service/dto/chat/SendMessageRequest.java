package com.sbms.chat_service.dto.chat;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SendMessageRequest {

	@NotNull
    private Long chatRoomId;   // context

    @NotBlank
    private String content;    // message body
}
