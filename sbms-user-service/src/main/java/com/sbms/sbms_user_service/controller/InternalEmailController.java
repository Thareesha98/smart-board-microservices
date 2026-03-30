package com.sbms.sbms_user_service.controller;

import com.sbms.sbms_user_service.dto.email.SendEmailRequest;
import com.sbms.sbms_user_service.dto.email.SendOtpEmailRequest;
import com.sbms.sbms_user_service.dto.email.SendPaymentReceiptEmailRequest;
import com.sbms.sbms_user_service.dto.email.SendResetPasswordEmailRequest;
import com.sbms.sbms_user_service.enums.EmailType;
import com.sbms.sbms_user_service.service.EmailService;

import java.util.Base64;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/internal/email")
public class InternalEmailController {

    private final EmailService emailService;

    public InternalEmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    // ---------------------------------------------------------
    // OTP EMAIL
    // ---------------------------------------------------------
    @PostMapping("/otp")
    public ResponseEntity<Void> sendOtp(@RequestBody SendOtpEmailRequest req) {
        emailService.sendOtpEmail(req.getEmail(), req.getOtp());
        return ResponseEntity.ok().build();
    }

    // ---------------------------------------------------------
    // RESET PASSWORD EMAIL
    // ---------------------------------------------------------
    @PostMapping("/reset-password")
    public ResponseEntity<Void> sendResetPassword(
            @RequestBody SendResetPasswordEmailRequest req
    ) {
        emailService.sendResetToken(req.getEmail(), req.getToken());
        return ResponseEntity.ok().build();
    }

    // ---------------------------------------------------------
    // PAYMENT RECEIPT EMAIL
    // ---------------------------------------------------------
    @PostMapping("/payment-receipt")
    public ResponseEntity<Void> sendPaymentReceipt(
            @RequestBody SendPaymentReceiptEmailRequest req
    ) {
        emailService.sendPaymentReceipt(
                req.getEmail(),
                req.getStudentName(),
                req.getPdfBytes(),
                req.getReceiptNumber()
        );
        return ResponseEntity.ok().build();
    }
    
    
    
    @PostMapping("/send")
    public ResponseEntity<Void> sendEmail(@RequestBody SendEmailRequest req) {

        EmailType type = req.getType();

        switch (type) {

            case OTP -> {
                String otp = (String) req.getData().get("otp");
                emailService.sendOtpEmail(req.getTo(), otp);
            }

            case PASSWORD_RESET -> {
                String token = (String) req.getData().get("token");
                emailService.sendResetToken(req.getTo(), token);
            }

            case PAYMENT_RECEIPT -> {

                String studentName =
                        (String) req.getData().get("studentName");

                String receiptNumber =
                        (String) req.getData().get("receiptNumber");

                String base64Pdf =
                        (String) req.getData().get("pdfBytes");

                byte[] pdfBytes =
                        Base64.getDecoder().decode(base64Pdf);

                emailService.sendPaymentReceipt(
                        req.getTo(),
                        studentName,
                        pdfBytes,
                        receiptNumber
                );
            }

            default ->
                throw new IllegalArgumentException(
                        "Unsupported email type: " + type
                );
        }

        return ResponseEntity.ok().build();
    }
    
    
    
}
