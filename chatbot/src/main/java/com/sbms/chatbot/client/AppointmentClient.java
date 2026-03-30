package com.sbms.chatbot.client;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.sbms.chatbot.dto.client.AppointmentResponseDTO;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentClient {

    private final WebClient appointmentWebClient;

    public List<AppointmentResponseDTO> getStudentAppointments(Long userId) {
        return appointmentWebClient.get()
                .uri("/api/appointments/student/{studentId}", userId)
                .header("X-User-Id", userId.toString())
                .header("X-User-Role", "STUDENT")
                .retrieve()
                .bodyToFlux(AppointmentResponseDTO.class)
                .collectList()
                .block();
    }
}