package com.sbms.chat_service.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.List;

@Component
public class WebSocketUserInterceptor implements ChannelInterceptor {

    private final ObjectMapper objectMapper;

    // Inject Spring's default ObjectMapper to parse the JSON payload
    public WebSocketUserInterceptor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null) return message;

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {

            String authHeader = accessor.getFirstNativeHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                System.out.println("❌ WS CONNECT missing Authorization");
                return null;
            }

            String token = authHeader.substring(7);

            try {
                // 1. A JWT has 3 parts separated by dots: header.payload.signature
                String[] chunks = token.split("\\.");
                if (chunks.length < 2) {
                    System.out.println("❌ Invalid JWT format");
                    return null;
                }

                // 2. Decode the Base64URL-encoded payload
                String payload = new String(Base64.getUrlDecoder().decode(chunks[1]));

                // 3. Parse the JSON payload
                JsonNode jwtPayload = objectMapper.readTree(payload);

                // 4. Extract data based on your exact payload structure
                String userId = jwtPayload.has("userId") ? jwtPayload.get("userId").asText() : null;
                String role = jwtPayload.has("role") ? jwtPayload.get("role").asText() : "USER";
                
                if (userId == null) {
                    System.out.println("❌ JWT payload is missing 'userId'");
                    return null;
                }

                // 5. Create the Authentication object, including their Role
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                userId, // We map the principal to userId so we can send messages to specific users
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_" + role))
                        );

                accessor.setUser(auth);
                System.out.println("✅ WS CONNECT SUCCESS -> USER = " + userId + " | ROLE = " + role);

            } catch (Exception e) {
                System.out.println("❌ Failed to parse JWT: " + e.getMessage());
                return null;
            }
        }

        return message;
    }
}