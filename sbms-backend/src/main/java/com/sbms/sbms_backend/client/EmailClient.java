package com.sbms.sbms_backend.client;

import java.time.Duration;
import java.util.Base64;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.sbms.sbms_backend.dto.user.SendEmailRequest;

@Service
public class EmailClient {

    private final WebClient webClient;

    public EmailClient(WebClient emailWebClient) {
        this.webClient = emailWebClient;
    }

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
                "pdfBytes",
                Base64.getEncoder().encodeToString(pdfBytes)
        ));

        webClient.post()
                .uri("/api/user/internal/email/send")
                .bodyValue(req)
                .retrieve()
                .toBodilessEntity()
                .timeout(Duration.ofSeconds(3))
                .block();
    }
}
