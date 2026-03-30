package com.sbms.chat_service.repository;


import com.sbms.chat_service.entity.ChatMessage;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface ChatMessageRepository extends JpaRepository<ChatMessage,Long>{

    Page<ChatMessage> findByChatRoomIdOrderByCreatedAtDesc(
            Long roomId,
            Pageable pageable
    );

    ChatMessage findTopByChatRoomIdOrderByCreatedAtDesc(Long roomId);

    @Query("""
        SELECT COUNT(m)
        FROM ChatMessage m
        WHERE m.chatRoomId=:roomId
        AND m.read=false
        AND m.senderId<>:userId
    """)
    long countUnreadMessages(
            @Param("roomId") Long roomId,
            @Param("userId") Long userId
    );

    @Modifying
    @Query("""
        UPDATE ChatMessage m
        SET m.read=true
        WHERE m.chatRoomId=:roomId
        AND m.senderId<>:userId
        AND m.read=false
    """)
    void markMessagesAsRead(
            @Param("roomId") Long roomId,
            @Param("userId") Long userId
    );
}