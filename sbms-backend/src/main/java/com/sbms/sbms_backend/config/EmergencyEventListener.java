package com.sbms.sbms_backend.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.sbms.sbms_backend.model.EmergencyTriggeredEvent;

import lombok.RequiredArgsConstructor;



@Component
@RequiredArgsConstructor
public class EmergencyEventListener {

    private final SimpMessagingTemplate messagingTemplate;

    @RabbitListener(queues = "emergency.queue")
    public void handleEmergency(EmergencyTriggeredEvent event) {
        System.out.println("RabbitMQ received event for owner: " + event.getOwnerId());

        // Using a dynamic topic path so only the specific owner gets the message
        // Destination: /topic/emergency.{ownerId}
        String destination = "/topic/emergency." + event.getOwnerId();
        
        messagingTemplate.convertAndSend(destination, event);
        
        System.out.println("WS sent to: " + destination);
    }
    
    
}