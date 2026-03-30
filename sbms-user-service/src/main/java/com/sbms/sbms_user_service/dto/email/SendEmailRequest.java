package com.sbms.sbms_user_service.dto.email;

import lombok.Data;

import java.util.Map;

import com.sbms.sbms_user_service.enums.EmailType;

@Data
public class SendEmailRequest {

    private EmailType type;

    // Mandatory
    private String to;

   
    private Map<String, Object> data;
}
