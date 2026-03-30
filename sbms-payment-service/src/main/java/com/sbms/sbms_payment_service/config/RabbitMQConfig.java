package com.sbms.sbms_payment_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    /**
     *  NEW (Spring AMQP 4+) JSON Message Converter
     * Replaces deprecated Jackson2JsonMessageConverter
     */
    @Bean
    public JacksonJsonMessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }

    /**
     *  CRITICAL: Configure RabbitTemplate to use JSON instead of SimpleMessageConverter
     */
    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            JacksonJsonMessageConverter messageConverter
    ) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }
    
    @Bean
    public Queue rollbackQueue() {
        return QueueBuilder.durable("sbms.payment.rollback.queue").build();
    }

    @Bean
    public Binding rollbackBinding(Queue rollbackQueue, TopicExchange exchange) {
        return BindingBuilder
                .bind(rollbackQueue)
                .to(exchange)
                .with("notification.failed");
    }
}