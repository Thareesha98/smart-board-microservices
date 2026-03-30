package com.sbms.chatbot.dto.client;


import com.sbms.chatbot.model.enums.UserRole;

import lombok.Data;

@Data
public class UserMinimalDTO {

    private Long id;
    private String fullName;
    private String email;
    private UserRole role;
    private boolean verifiedOwner;
}