package com.interview.platform.account.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RabbitTaskPublisher {

    private final RabbitTemplate rabbitTemplate;

    public RabbitTaskPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendEmailTask(String message) {
        // Queue style async task (good for worker processing and retries).
        rabbitTemplate.convertAndSend("interview.exchange", "email.send", message);
    }
}
