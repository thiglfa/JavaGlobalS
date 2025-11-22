package com.wellwork.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String QUEUE_WELCOME = "user.welcome.queue";
    public static final String EXCHANGE = "user.exchange";
    public static final String ROUTING_KEY = "user.welcome";

    @Bean
    public Queue queue() {
        return new Queue(QUEUE_WELCOME, true);
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Binding binding() {
        return BindingBuilder.bind(queue()).to(exchange()).with(ROUTING_KEY);
    }
}
