package com.wellwork.messaging;

import com.wellwork.config.RabbitConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class WelcomeMessageProducer {

    private final RabbitTemplate rabbitTemplate;

    public WelcomeMessageProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendWelcomeMessage(String username) {
        String message = "Bem-vindo ao WellWork, " + username + "!";
        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE,
                RabbitConfig.ROUTING_KEY,
                message
        );
    }
}
