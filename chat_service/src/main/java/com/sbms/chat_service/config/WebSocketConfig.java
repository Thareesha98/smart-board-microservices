package com.sbms.chat_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.*;
import org.springframework.web.socket.config.annotation.*;

import lombok.RequiredArgsConstructor;






@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketUserInterceptor userInterceptor;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        registry
                .addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS()
                .setSessionCookieNeeded(false);
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        registry.enableSimpleBroker("/topic", "/queue");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {

        registration.interceptors(userInterceptor);
    }
}