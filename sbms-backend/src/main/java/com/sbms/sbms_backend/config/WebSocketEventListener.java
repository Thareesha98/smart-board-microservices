package com.sbms.sbms_backend.config;



import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {

    @EventListener
    public void handleConnect(SessionConnectEvent event) {
        System.out.println("WS CLIENT CONNECTED: " + event);
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        System.out.println("WS CLIENT DISCONNECTED: " + event);
    }
}