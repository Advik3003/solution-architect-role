package com.interview.platform.account.processing;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransferAnalyticsProcessor {

    @KafkaListener(topics = "interview-events", groupId = "analytics-processor")
    public void process(String payload) {
        // Placeholder for stream processing logic (fraud scoring, BI metrics, etc.).
        // In real systems this would transform and push data into warehouse/lakehouse.
        System.out.println("Analytics processor received: " + payload);
    }
}
