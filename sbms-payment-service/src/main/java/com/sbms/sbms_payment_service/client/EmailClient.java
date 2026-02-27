package com.sbms.sbms_payment_service.client;

import java.time.Duration;
import java.util.Base64;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.sbms.sbms_payment_service.dto.user.SendEmailRequest;

import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmailClient {

    private final WebClient webClient;

    public EmailClient(WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl("http://user-service:8080")
                .build();
    }

    @Retry(name = "emailService", fallbackMethod = "fallbackEmail")
    public void sendPaymentReceipt(
            String email,
            String studentName,
            String receiptNumber,
            byte[] pdfBytes
    ) {

        SendEmailRequest req = new SendEmailRequest();
        req.setType("PAYMENT_RECEIPT");
        req.setTo(email);
        req.setData(Map.of(
                "studentName", studentName,
                "receiptNumber", receiptNumber,
                "pdfBytes", Base64.getEncoder().encodeToString(pdfBytes)
        ));

        log.info("Sending receipt email to {}", email);

        webClient.post()
                .uri("/api/internal/email/send")
                .bodyValue(req)
                .retrieve()
                .toBodilessEntity()
                .timeout(Duration.ofSeconds(3))
                .block();
    }

    public void fallbackEmail(
            String email,
            String studentName,
            String receiptNumber,
            byte[] pdfBytes,
            Throwable ex
    ) {
        log.error("Email service DOWN. Receipt email skipped for {}", email, ex);
    }
}