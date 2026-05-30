package com.interview.platform.account.messaging;

import java.util.Map;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OrderEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(String eventType, Map<String, Object> payload) {
        // Key by eventType for demo; real systems often key by entity id for partition ordering.
        kafkaTemplate.send("interview-events", eventType, payload);
    }
}
