package com.sbms.chat_service.service;



import com.sbms.chat_service.client.BoardingClient;
import com.sbms.chat_service.client.BoardingSnapshot;
import com.sbms.chat_service.client.UserClient;
import com.sbms.chat_service.dto.user.UserMinimalDTO;
import com.sbms.chat_service.entity.ChatListItem;
import com.sbms.chat_service.entity.ChatMessage;
import com.sbms.chat_service.entity.ChatMessageSentEvent;
import com.sbms.chat_service.entity.ChatRoom;
import com.sbms.chat_service.events.ChatEventPublisher;
import com.sbms.chat_service.repository.ChatMessageRepository;
import com.sbms.chat_service.repository.ChatRoomRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomService chatRoomService;
    private final ChatEventPublisher eventPublisher;
    
    private final UserClient userClient;
    private final BoardingClient boardingClient;

  
    public ChatMessage sendMessage(
            Long roomId,
            Long senderId,
            ChatMessage.SenderRole role,
            String content
    ) {

        ChatRoom room =
                chatRoomRepository.findById(roomId)
                        .orElseThrow(() ->
                                new IllegalArgumentException("Chat room not found")
                        );

        chatRoomService.validateParticipant(room, senderId);

        ChatMessage message = ChatMessage.builder()
                .chatRoomId(roomId)
                .senderId(senderId)
                .senderRole(role)
                .content(content)
                .read(false)
                .build();

        ChatMessage saved =
                chatMessageRepository.save(message);

        room.setLastMessageAt(saved.getCreatedAt());

        chatRoomRepository.save(room);

        /* -------------------------------------------
           DETERMINE RECEIVER
        ------------------------------------------- */

        Long receiverId =
                senderId.equals(room.getStudentId())
                        ? room.getOwnerId()
                        : room.getStudentId();

        /* -------------------------------------------
           PUBLISH EVENT
        ------------------------------------------- */

        ChatMessageSentEvent event =
                ChatMessageSentEvent.builder()
                        .messageId(saved.getId())
                        .chatRoomId(roomId)
                        .senderId(senderId)
                        .receiverId(receiverId)
                        .content(content)
                        .createdAt(saved.getCreatedAt())
                        .build();

        eventPublisher.publishMessageSent(event);

        return saved;
    }
    
    
    public List<ChatListItem> getChatList(Long userId) {

        List<ChatRoom> rooms =
                chatRoomRepository.findByStudentIdOrOwnerIdOrderByLastMessageAtDesc(
                        userId,
                        userId
                );

        return rooms.stream().map(room -> {

            ChatMessage last =
                    chatMessageRepository
                            .findTopByChatRoomIdOrderByCreatedAtDesc(room.getId());

            long unread =
                    chatMessageRepository
                            .countUnreadMessages(room.getId(), userId);

            String lastMessage =
                    last != null ? last.getContent() : null;

            // 1. Determine who the OTHER user is
            Long otherUserId = userId.equals(room.getStudentId()) 
                    ? room.getOwnerId() 
                    : room.getStudentId();

            // 2. Fetch real user name via UserClient
            UserMinimalDTO otherUser = userClient.getUserMinimal(otherUserId);
            String otherUserName = (otherUser != null && otherUser.fullName() != null) 
                    ? otherUser.fullName() 
                    : "Unknown User";

            // 3. Fetch real boarding title via BoardingClient
            String boardingTitle = "Boarding #" + room.getBoardingId(); // Default fallback
            try {
                BoardingSnapshot boarding = boardingClient.getBoarding(room.getBoardingId());
                if (boarding != null && boarding.title() != null) {
                    boardingTitle = boarding.title();
                }
            } catch (Exception e) {
                log.warn("Failed to fetch boarding details for ID {}", room.getBoardingId());
            }

            return new ChatListItem(
                    room.getId(),
                    otherUserName,
                    boardingTitle,
                    lastMessage,
                    unread
            );

        }).toList();
    }
    
    


    public Page<ChatMessage> loadMessages(Long roomId, Pageable pageable){
        return chatMessageRepository
                .findByChatRoomIdOrderByCreatedAtDesc(roomId,pageable);
    }

    public void markAsRead(Long roomId,Long userId){
        chatMessageRepository.markMessagesAsRead(roomId,userId);
    }
}