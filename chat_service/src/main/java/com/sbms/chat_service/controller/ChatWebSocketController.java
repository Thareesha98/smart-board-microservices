package com.sbms.chat_service.controller;

import com.sbms.chat_service.entity.ChatMessage;
import com.sbms.chat_service.service.ChatService;

import lombok.RequiredArgsConstructor;

import java.security.Principal;

import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.send")
    public void sendMessage(
            ChatMessage request,
            Principal principal
    ) {

        if (principal == null) {
            throw new IllegalStateException("Unauthenticated WebSocket message");
        }

        Long senderId = Long.valueOf(principal.getName());

        ChatMessage saved = chatService.sendMessage(
                request.getChatRoomId(),
                senderId,
                request.getSenderRole(),
                request.getContent()
        );

        messagingTemplate.convertAndSend(
                "/topic/chat/" + saved.getChatRoomId(),
                saved
        );
    }
}
    
    
//    @PostMapping
//    public StartChatResponse startChat(@RequestBody StartChatRequest request){
//
//        ChatRoom room=chatRoomService.getOrCreateRoom(
//                request.getStudentId(),
//                request.getOwnerId(),
//                request.getBoardingId()
//        );
//
//        return new StartChatResponse(room.getId());
//    }
