package com.sbms.chat_service.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

@Configuration
public class RabbitMQConfig {

    @Value("${sbms.rabbitmq.exchange:sbms.events}")
    private String exchangeName;

    /* ------------------------------------------------
       GLOBAL OBJECT MAPPER
    ------------------------------------------------ */

    @Bean
    public ObjectMapper objectMapper() {

        ObjectMapper mapper = new ObjectMapper();

        mapper.registerModule(new JavaTimeModule());

        mapper.disable(
                SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
        );

        return mapper;
    }

    /* ------------------------------------------------
       EXCHANGE
    ------------------------------------------------ */

    @Bean
    public TopicExchange eventExchange() {
        return new TopicExchange(exchangeName, true, false);
    }

    /* ------------------------------------------------
       MESSAGE CONVERTER
    ------------------------------------------------ */

    @Bean
    public MessageConverter jsonConverter(ObjectMapper mapper) {
        return new Jackson2JsonMessageConverter(mapper);
    }

    /* ------------------------------------------------
       RABBIT TEMPLATE
    ------------------------------------------------ */

    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            MessageConverter converter
    ) {

        RabbitTemplate template = new RabbitTemplate(connectionFactory);

        template.setMessageConverter(converter);

        return template;
    }

    /* ------------------------------------------------
       CHAT QUEUE
    ------------------------------------------------ */

    @Bean
    public Queue chatEventQueue() {
        return new Queue("chat.events.queue", true);
    }

    @Bean
    public Binding chatEventBinding(
            Queue chatEventQueue,
            TopicExchange exchange
    ) {

        return BindingBuilder
                .bind(chatEventQueue)
                .to(exchange)
                .with("chat.*");
    }

    /* ------------------------------------------------
       EMERGENCY QUEUE (same as monolith)
    ------------------------------------------------ */

    @Bean
    public Queue emergencyQueue() {
        return new Queue("emergency.queue", true);
    }

    @Bean
    public Binding emergencyBinding(
            Queue emergencyQueue,
            TopicExchange exchange
    ) {

        return BindingBuilder
                .bind(emergencyQueue)
                .to(exchange)
                .with("emergency.*");
    }

}