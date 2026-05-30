# API Gateway

> Stack reference: `unified-stack-standard-springboot-aws.md`

## What It Is
API Gateway is the single entry point for client requests to backend services.

## Why We Use It
- Centralized security and routing.
- Unified API exposure to clients.
- Better monitoring and governance.

## What We Use
- Spring Cloud Gateway / Kong / NGINX
- JWT validation, rate limiting, request filters
- Route policies and response transformation

## How to Implement
1. Define external API routes and map to internal services.
2. Enable authentication and token validation at gateway.
3. Apply quotas/rate limits per client or API key.
4. Add request/response logging with correlation ID.
5. Configure retries/timeouts carefully.
6. Publish API docs and deprecation policy.

## Achievements
- More secure and manageable API surface.
- Consistent client experience.
- Easier traffic control and analytics.

## Important Code Example

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: account-service
          uri: lb://ACCOUNT-SERVICE # Uses discovery + load balancing
          predicates:
            - Path=/api/v1/accounts/** # Route only account APIs
          filters:
            - StripPrefix=2 # Removes /api/v1 before forwarding
```

```java
@Bean
public GlobalFilter correlationIdFilter() {
    return (exchange, chain) -> {
        // Adds trace id so logs can track one request across services
        exchange.getRequest().mutate().header("X-Correlation-Id", UUID.randomUUID().toString()).build();
        return chain.filter(exchange);
    };
}
```
