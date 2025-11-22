package com.wellwork.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class WelcomeListener {

    @RabbitListener(queues = "user.welcome.queue")
    public void receiveWelcomeMessage(String message) {
        System.out.println("📩 [LISTENER] Mensagem recebida: " + message);
    }
}
