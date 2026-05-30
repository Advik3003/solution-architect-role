package com.interview.platform.gateway;

import java.util.UUID;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CorrelationIdFilter {

    @Bean
    public GlobalFilter correlationHeaderFilter() {
        return (exchange, chain) -> {
            // Why this exists:
            // Correlation id makes distributed debugging practical across gateway and downstream services.
            // Interview talking point:
            // "I treat observability as a first-class requirement, not a post-production add-on."
            // Create one unique id per incoming request for distributed tracing.
            String correlationId = UUID.randomUUID().toString();
            exchange.getRequest().mutate().header("X-Correlation-Id", correlationId).build();
            return chain.filter(exchange);
        };
    }
}
