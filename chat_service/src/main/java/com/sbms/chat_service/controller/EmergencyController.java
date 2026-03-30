package com.sbms.chat_service.controller;




import lombok.RequiredArgsConstructor;

import java.security.Principal;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import com.sbms.chat_service.client.BoardingClient;
import com.sbms.chat_service.client.UserClient;
import com.sbms.chat_service.events.EmergencyEventRequest;




@RestController
@RequestMapping("/api/emergency")
@RequiredArgsConstructor
public class EmergencyController {

    private final SimpMessagingTemplate messagingTemplate;
    private final BoardingClient boardingClient;
    private final UserClient userClient ;
    @PostMapping("/push")
    public void pushEmergency(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody EmergencyEventRequest request
    ) {

        if (userId == null) {
            throw new RuntimeException("Unauthenticated request");
        }

        request.setUserId(userId);

        Long ownerId = boardingClient.getOwnerId(request.getBoardingId());

        if (ownerId == null) {
            throw new RuntimeException("Owner not found");
        }

        var user = userClient.getUserMinimal(userId);
        var boarding = boardingClient.getBoarding(request.getBoardingId());

        request.setMessage(
            "Emergency from " + user.fullName() +
            " at " + boarding.title()
        );

        messagingTemplate.convertAndSendToUser(
                ownerId.toString(),
                "/queue/emergency",
                request
        );
    }
}