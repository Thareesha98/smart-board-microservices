package com.sbms.chat_service.events;
import lombok.RequiredArgsConstructor;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.sbms.chat_service.entity.ChatMessageSentEvent;

@Component
@RequiredArgsConstructor
public class ChatEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${sbms.rabbitmq.exchange:sbms.events}")
    private String exchange;

    public void publishMessageSent(ChatMessageSentEvent event) {

        rabbitTemplate.convertAndSend(
                exchange,
                "chat.message.sent",
                event
        );
    }

}