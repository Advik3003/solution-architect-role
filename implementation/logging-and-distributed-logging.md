# Logging and Distributed Logging

> Stack reference: `unified-stack-standard-springboot-aws.md`

## What It Is
Logging captures system events; distributed logging correlates logs across multiple services.

## Why We Use It
- Troubleshoot production issues quickly.
- Trace user requests across microservices.
- Support security and audit investigations.

## What We Use
- Structured JSON logs
- Correlation/trace IDs
- ELK/OpenSearch + centralized log pipeline

## How to Implement
1. Standardize log format across services.
2. Inject `correlationId` at gateway and propagate downstream.
3. Log key events: auth attempts, API errors, external call failures.
4. Mask sensitive fields (PII, tokens, card data).
5. Ship logs to centralized platform with retention policy.
6. Build searchable dashboards and alerts.

## Achievements
- Faster root cause analysis.
- Better incident response.
- Audit-friendly operational visibility.

## Important Code Example

```java
public class CorrelationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws ServletException, IOException {
        String correlationId = Optional.ofNullable(request.getHeader("X-Correlation-Id"))
            .orElse(UUID.randomUUID().toString());
        MDC.put("correlationId", correlationId); // Adds value to all logs in this request scope
        response.setHeader("X-Correlation-Id", correlationId);
        chain.doFilter(request, response);
        MDC.clear(); // Prevents context leak to next request thread
    }
}
```
