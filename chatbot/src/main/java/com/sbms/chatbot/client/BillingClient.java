package com.sbms.chatbot.client;



import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.sbms.chatbot.dto.client.MonthlyBillResponseDTO;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BillingClient {

    private final WebClient billingWebClient;

    public List<MonthlyBillResponseDTO> getStudentBills(Long userId) {

        System.out.println("🔥 CALLING BILLING SERVICE FOR USER: " + userId);

        List<MonthlyBillResponseDTO> result = billingWebClient.get()
                .uri("/api/bills/internal/student/{userId}", userId)
                .retrieve()
                .bodyToFlux(MonthlyBillResponseDTO.class)
                .collectList()
                .block();

        System.out.println("🔥 BILLING RESPONSE: " + result);

        return result;
    }
}