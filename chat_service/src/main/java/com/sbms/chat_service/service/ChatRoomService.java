package com.sbms.chat_service.service;


import com.sbms.chat_service.client.BoardingClient;
import com.sbms.chat_service.client.BoardingSnapshot;
import com.sbms.chat_service.entity.ChatRoom;
import com.sbms.chat_service.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final BoardingClient boardingClient;
    
    
    public List<ChatRoom> getUserRooms(Long userId) {

        return chatRoomRepository
                .findByStudentIdOrOwnerIdOrderByLastMessageAtDesc(
                        userId,
                        userId
                );
    }
    

    public ChatRoom getOrCreateRoom(
            Long studentId,
            Long ownerId,
            Long boardingId
    ){

        return chatRoomRepository
                .findByStudentIdAndOwnerIdAndBoardingId(
                        studentId,
                        ownerId,
                        boardingId
                )
                .orElseGet(() ->
                        chatRoomRepository.save(
                                ChatRoom.builder()
                                        .studentId(studentId)
                                        .ownerId(ownerId)
                                        .boardingId(boardingId)
                                        .build()
                        )
                );
    }

    public void validateParticipant(ChatRoom room,Long userId){

        if(!room.getStudentId().equals(userId)
                && !room.getOwnerId().equals(userId)){
            throw new SecurityException("User not in this chat room");
        }
    }
    
    
    public ChatRoom getOrCreateRoom(
            Long studentId,
            Long boardingId
    ){

        BoardingSnapshot boarding =
                boardingClient.getBoarding(boardingId);

        Long ownerId = boarding.ownerId();

        return chatRoomRepository
                .findByStudentIdAndOwnerIdAndBoardingId(
                        studentId,
                        ownerId,
                        boardingId
                )
                .orElseGet(() ->
                        chatRoomRepository.save(
                                ChatRoom.builder()
                                        .studentId(studentId)
                                        .ownerId(ownerId)
                                        .boardingId(boardingId)
                                        .build()
                        )
                );
    }
    
    
}