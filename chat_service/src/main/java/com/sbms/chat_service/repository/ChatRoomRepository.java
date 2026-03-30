package com.sbms.chat_service.repository;


import com.sbms.chat_service.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom,Long>{

    Optional<ChatRoom> findByStudentIdAndOwnerIdAndBoardingId(
            Long studentId,
            Long ownerId,
            Long boardingId
    );

    List<ChatRoom> findByStudentIdOrOwnerIdOrderByLastMessageAtDesc(
            Long studentId,
            Long ownerId
    );
    
    
} 