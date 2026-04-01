package com.consumer.consumer;

import com.produser.produser.RabbitMQconfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConsumerService {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private Queue queue;

    @RabbitListener(queues = "myQueue")
    public void receiveMessage(String message){
        System.out.println("Receive: " + message);
    }

    public void sendMessage(String message){
        rabbitTemplate.contextAndSend(queue.getName(), message);
        System.out.println("sent: " + message);
    }

}
