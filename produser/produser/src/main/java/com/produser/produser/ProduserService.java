package com.produser.produser;

import java.util.Queue;

import com.produser.produser.RabbitMQconfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProduserService {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private Queue queue;

    public void sendMessage(String message){
        rabbitTemplate.convertAndSend(queue.getName(),message);
        System.out.println("sent" + message);
    }

}
