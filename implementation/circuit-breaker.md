# Circuit Breaker

> Stack reference: `unified-stack-standard-springboot-aws.md`

## What It Is
Circuit breaker prevents repeated calls to failing downstream services.

## Why We Use It
- Avoid cascading failures.
- Protect system stability under dependency outages.
- Improve recovery behavior.

## What We Use
- Resilience4j / Hystrix-like patterns
- Timeout, retry, fallback, bulkhead policies

## How to Implement
1. Identify all external and cross-service calls.
2. Set timeout limits based on SLOs.
3. Configure circuit breaker thresholds.
4. Add controlled retries with exponential backoff.
5. Implement fallback behavior for non-critical features.
6. Monitor open/half-open states and recovery times.

## Achievements
- Better uptime under partial failures.
- Controlled latency during dependency issues.
- Faster stabilization in incidents.

## Important Code Example

```java
@Service
public class PaymentClient {

    @CircuitBreaker(name = "paymentService", fallbackMethod = "fallback")
    @Retry(name = "paymentService")
    @TimeLimiter(name = "paymentService")
    public CompletableFuture<String> callPaymentApi() {
        // Simulates outbound dependency call
        return CompletableFuture.supplyAsync(() -> restTemplate.getForObject("/pay", String.class));
    }

    public CompletableFuture<String> fallback(Throwable ex) {
        // Returned when circuit is open or dependency fails repeatedly
        return CompletableFuture.completedFuture("Payment service temporarily unavailable");
    }
}
```
