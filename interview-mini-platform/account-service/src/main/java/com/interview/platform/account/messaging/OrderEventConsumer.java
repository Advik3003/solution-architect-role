package com.interview.platform.account.messaging;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventConsumer {

    @KafkaListener(topics = "interview-events", groupId = "audit-consumer")
    public void consume(String payload) {
        // Interview point: asynchronous side processing without blocking caller path.
        System.out.println("Kafka event consumed: " + payload);
    }
}
