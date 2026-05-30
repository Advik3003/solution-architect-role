# Kafka

> Stack reference: `unified-stack-standard-springboot-aws.md`

## What It Is
Kafka is a distributed event streaming platform for high-throughput, fault-tolerant messaging.

## Why We Use It
- Decouple services with asynchronous communication.
- Handle high event volume (orders, payments, notifications).
- Support replay and stream processing.

## What We Use
- Topics, partitions, consumer groups
- Schema registry for event contracts
- DLQ pattern for poison events

## How to Implement
1. Define event names and payload schema with versioning.
2. Create topics by domain (`order-events`, `payment-events`).
3. Use producer idempotence and proper ACK settings.
4. Build consumers with retry + dead-letter handling.
5. Add monitoring for lag, throughput, and failures.
6. Use partition keys (e.g., `orderId`) to preserve ordering where needed.

## Achievements
- Scalable async workflows.
- Better resilience during spikes.
- Easier integration with analytics and data pipelines.

## Important Code Example

```java
public void publishOrderEvent(OrderEvent event) {
    // Sends event to Kafka topic; key keeps same order events in same partition
    kafkaTemplate.send("order-events", event.getOrderId(), event);
}
```

```java
@KafkaListener(topics = "order-events", groupId = "notification-service")
public void consumeOrderEvent(OrderEvent event) {
    // Reacts asynchronously to order changes without blocking checkout flow
    notificationSender.sendOrderUpdate(event);
}
```
