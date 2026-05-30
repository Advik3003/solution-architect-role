# AWS SQS

> Stack reference: `unified-stack-standard-springboot-aws.md`

## What It Is
Amazon SQS is a managed message queue for asynchronous processing.

## Why We Use It
- Decouples services and smooths traffic spikes.
- Reliable message delivery with retry support.
- Easy to run without broker maintenance.

## What We Use
- Standard queue for high throughput
- FIFO queue for strict ordering/deduplication
- DLQ for failed processing

## How to Implement
1. Define async tasks suitable for queue processing.
2. Choose Standard or FIFO queue based on ordering requirement.
3. Tune visibility timeout and long polling.
4. Configure dead-letter queue and max receive count.
5. Build idempotent consumers.
6. Monitor queue depth and processing latency.

## Achievements
- Better reliability for background jobs.
- Reduced coupling and improved scalability.
- Easier failure isolation and retries.

## Important Code Example

```java
public void publishMessage(String queueUrl, String payload) {
    SendMessageRequest req = SendMessageRequest.builder()
        .queueUrl(queueUrl)
        .messageBody(payload) // Business event payload
        .build();
    sqsClient.sendMessage(req); // Pushes async work to queue
}
```

```java
@SqsListener("order-processing-queue")
public void consume(String message) {
    // Handle message idempotently to avoid duplicate side effects
    System.out.println("Received: " + message);
}
```
