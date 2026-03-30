package com.sbms.chat_service.consumer;

import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.sbms.chat_service.entity.ChatMessageSentEvent;

@Component
@Slf4j
public class ChatEventConsumer {

    @RabbitListener(queues = "chat.events.queue")
    public void handleChatEvent(ChatMessageSentEvent event) {

        log.info("New chat message received: {}", event);

        // send push notification
        // send email
        // send websocket event

    }
}