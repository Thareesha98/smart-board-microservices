package com.sbms.sbms_payment_service.dto.user;

import java.util.Map;

import lombok.Data;

@Data
public class SendEmailRequest {
    private String type;
    private String to;
    private Map<String, Object> data;
}
