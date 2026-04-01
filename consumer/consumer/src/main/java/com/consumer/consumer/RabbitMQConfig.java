package com.consumer.consumer;

import org.springframework.amqp.core.Queeu;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQconfig {

    @Bean
    public Queue mQueue(){
        return new Query("myQueue", false);
    }
}