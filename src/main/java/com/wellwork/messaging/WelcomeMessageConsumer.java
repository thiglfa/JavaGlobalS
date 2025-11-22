package com.wellwork.messaging;

import com.wellwork.config.RabbitConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class WelcomeMessageConsumer {

    @RabbitListener(queues = RabbitConfig.QUEUE_WELCOME)
    public void receiveWelcomeMessage(String message) {
        System.out.println("📩 Mensagem recebida: " + message);
        // Aqui você poderia enviar e-mail, SMS, notificação, etc.
    }
}
