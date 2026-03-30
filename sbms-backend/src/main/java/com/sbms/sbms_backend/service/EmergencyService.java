package com.sbms.sbms_backend.service;

//import java.time.LocalDateTime;
//
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import com.sbms.sbms_backend.client.BoardingClient;
//import com.sbms.sbms_backend.client.UserClient;
//import com.sbms.sbms_backend.dto.user.UserMinimalDTO;
//import com.sbms.sbms_backend.model.EmergencyTriggeredEvent;
//import com.sbms.sbms_backend.record.BoardingSnapshot;
//
//
//import lombok.RequiredArgsConstructor;
//
//@Service
//@RequiredArgsConstructor
//public class EmergencyService {
//
//    private final RabbitTemplate rabbitTemplate;
//    private final BoardingClient boardingClient;
//    private final UserClient userClient;
//
//
//    @Value("${sbms.rabbitmq.exchange}")
//    private String exchange;
//
//    public void trigger(Long studentId, Long boardingId,
//    		 Double latitude,
//             Double longitude) {
//
//    	 BoardingSnapshot boarding = boardingClient.getBoarding(boardingId);
//
//         if (boarding == null) {
//             throw new RuntimeException("Boarding not found");
//         }
//
//         // CALL USER MICROSERVICE
//         UserMinimalDTO student = userClient.getUserMinimal(studentId);
//
//         if (student == null) {
//             throw new RuntimeException("User not found");
//         }
//
//         EmergencyTriggeredEvent event =
//                 new EmergencyTriggeredEvent(
//                         studentId,
//                         boarding.ownerId(),
//                         boardingId,
//                         student.getFullName(),
//                         boarding.title(),
//                         "PANIC_BUTTON",
//                         LocalDateTime.now(),
//                         latitude,
//                         longitude
//                 );
//
//         System.out.println("Emergency event sent");
//
//
//         rabbitTemplate.convertAndSend(
//                 exchange,
//                 "emergency.triggered",
//                 event
//         );
//    }
//}
    
    
    
    
    
    
    
    

    import com.sbms.sbms_backend.client.ChatClient;
import com.sbms.sbms_backend.events.EmergencyEventRequest;

import lombok.RequiredArgsConstructor;
    import org.springframework.stereotype.Service;

    @Service
    @RequiredArgsConstructor
    public class EmergencyService {

        private final ChatClient chatClient;

        public void trigger(
                Long userId,
                Long boardingId,
                Double lat,
                Double lon
        ) {

            // 🔥 create request
            EmergencyEventRequest request = new EmergencyEventRequest();
            request.setUserId(userId);
            request.setBoardingId(boardingId);
            request.setLatitude(lat);
            request.setLongitude(lon);
            request.setMessage("🚨 Emergency triggered!");

            // 🔥 call chat-service
            chatClient.sendEmergency(request);
        }
    }
