package com.backend.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author mqz
 */
@Configuration
public class RabbitConfig {


    @Bean("mailQueue")
    public Queue queue() {
        return QueueBuilder.durable("mail").build();
    }

}
