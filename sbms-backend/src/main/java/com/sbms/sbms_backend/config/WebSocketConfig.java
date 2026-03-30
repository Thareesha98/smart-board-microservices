package com.sbms.sbms_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;


import lombok.RequiredArgsConstructor;


@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Enables an in-memory broker to send messages back to the client on destinations prefixed with /topic and /queue
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
        
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // The endpoint the frontend will connect to (e.g., ws://localhost:8080/ws)
        registry.addEndpoint("/backend-ws")
                .setAllowedOriginPatterns("*") // Configure CORS as needed
                .withSockJS()                 // ✅ THIS FIXES EVERYTHING
                .setSessionCookieNeeded(false); 
                ;
        
       
    }
}