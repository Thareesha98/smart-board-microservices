package com.sbms.sbms_user_service.dto.email;

import lombok.Data;

@Data
public class SendPaymentReceiptEmailRequest {

    private String email;
    private String studentName;
    private byte[] pdfBytes;
    private String receiptNumber;
}
