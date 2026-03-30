package com.sbms.chat_service.dto.user;


public record UserMinimalDTO(
        Long id,
        String fullName,
        String email,
        String role,
        boolean verifiedOwner
) {}