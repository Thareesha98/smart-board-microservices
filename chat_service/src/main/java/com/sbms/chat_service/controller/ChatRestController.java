package com.sbms.chat_service.controller;


import com.sbms.chat_service.dto.chat.StartChatRequest;
import com.sbms.chat_service.dto.chat.StartChatResponse;
import com.sbms.chat_service.entity.ChatListItem;
import com.sbms.chat_service.entity.ChatMessage;
import com.sbms.chat_service.entity.ChatRoom;
import com.sbms.chat_service.service.ChatRoomService;
import com.sbms.chat_service.service.ChatService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatRestController {

    private final ChatRoomService chatRoomService;
    private final ChatService chatService;
    
    
    @GetMapping
    public ResponseEntity<List<ChatListItem>> getChatList(
            @RequestHeader("X-User-Id") Long userId
    ) {

        return ResponseEntity.ok(
                chatService.getChatList(userId)
        );
    }

   
    @PostMapping
    public ResponseEntity<StartChatResponse> startChat(
            @RequestBody @Valid StartChatRequest request,
            @RequestHeader("X-User-Id") Long studentId,
            @RequestHeader("X-User-Role") String role
    ) {

        if (!"STUDENT".equals(role)) {
            throw new IllegalStateException("Only students can start chats");
        }

        ChatRoom room = chatRoomService.getOrCreateRoom(
                studentId,
                request.getBoardingId()
        );

        return ResponseEntity.ok(
                new StartChatResponse(room.getId())
        );
    }

    
    @GetMapping("/{roomId}/messages")
    public ResponseEntity<Page<ChatMessage>> getMessages(
            @PathVariable Long roomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {

        Pageable pageable = PageRequest.of(page, size);

        Page<ChatMessage> messages =
                chatService.loadMessages(roomId, pageable);

        return ResponseEntity.ok(messages);
    }

    
    @PatchMapping("/{roomId}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long roomId,
            @RequestHeader("X-User-Id") Long userId
    ) {

        chatService.markAsRead(roomId, userId);

        return ResponseEntity.noContent().build();
    }
}