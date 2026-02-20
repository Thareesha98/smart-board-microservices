package com.sbms.sbms_payment_service.config;




import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;







@Configuration
public class PaymentRabbitMQTopologyConfig {

    public static final String EXCHANGE = "sbms.events";

    // DLQ
    @Bean
    public Queue paymentDlq() {
        return QueueBuilder.durable("payment.dlq").build();
    }

    @Bean
    public DirectExchange paymentDlx() {
        return new DirectExchange("payment.dlx");
    }

    // (Optional internal retry queue)
    @Bean
    public Queue paymentRetryQueue() {
        return QueueBuilder.durable("payment.retry")
                .withArgument("x-dead-letter-exchange", EXCHANGE)
                .build();
    }
}
