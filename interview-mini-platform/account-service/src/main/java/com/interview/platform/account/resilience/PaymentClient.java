package com.interview.platform.account.resilience;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class PaymentClient {

    private final RestClient restClient = RestClient.builder()
        .baseUrl("http://payment-provider.local")
        .build();

    @CircuitBreaker(name = "payment", fallbackMethod = "fallback")
    @Retry(name = "payment")
    public Map<String, Object> checkHealth() {
        // Why this exists:
        // Downstream dependencies fail; resilience policies prevent cascading outages.
        // Interview talking point:
        // "I combine timeout/retry/circuit-breaker so one weak dependency cannot take down the platform."
        // External dependency call that may fail intermittently.
        return restClient.get().uri("/health").retrieve().body(Map.class);
    }

    public Map<String, Object> fallback(Exception ex) {
        // Controlled fallback keeps system responsive when dependency is down.
        return Map.of("status", "DEGRADED", "reason", "Payment provider unavailable");
    }
}
