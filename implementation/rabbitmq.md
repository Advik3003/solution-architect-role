# RabbitMQ

> Stack reference: `unified-stack-standard-springboot-aws.md`

## What It Is
RabbitMQ is a message broker optimized for reliable queue-based messaging patterns.

## Why We Use It
- Reliable task queues and worker processing.
- Strong routing options (direct, topic, fanout exchanges).
- Good fit for command-style async workflows.

## What We Use
- Exchanges + queues + routing keys
- Publisher confirms and durable queues
- Dead-letter exchanges

## How to Implement
1. Model message flows by business action.
2. Create exchanges and bind queues with routing keys.
3. Enable durable queues and persistent messages.
4. Implement consumer ACK/NACK with retry logic.
5. Configure dead-letter queue for failed messages.
6. Monitor queue depth and consumer throughput.

## Achievements
- Reliable background processing.
- Reduced coupling between producers and consumers.
- Better control over retry and failure handling.

## Important Code Example

```java
public void sendEmailTask(String payload) {
    // Publishes task to exchange with routing key
    rabbitTemplate.convertAndSend("notification.exchange", "email.send", payload);
}
```

```java
@RabbitListener(queues = "email-queue")
public void receive(String payload) {
    // Worker processes queued task asynchronously
    emailService.send(payload);
}
```
