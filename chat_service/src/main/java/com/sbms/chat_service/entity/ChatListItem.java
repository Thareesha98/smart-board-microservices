package com.sbms.chat_service.entity;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatListItem {

    private Long chatRoomId;
    private String otherUserName;
    private String boardingTitle;
    private String lastMessage;
    private long unreadCount;

}