# Spring Cloud

> Stack reference: `unified-stack-standard-springboot-aws.md`

## What It Is
Spring Cloud provides tools for microservice patterns like config management, service discovery, gateway routing, and resilience.

## Why We Use It
- Speeds up microservice platform setup.
- Standardizes cloud-native patterns across teams.
- Reduces boilerplate for distributed system features.

## What We Use
- Spring Cloud Config
- Spring Cloud Gateway
- Eureka (or Consul) for discovery
- OpenFeign for service calls
- Resilience4j integration

## How to Implement
1. Create a config server and centralize environment configs.
2. Register all services to discovery server.
3. Route external traffic via API gateway.
4. Use Feign clients for service-to-service calls.
5. Add circuit breaker/retry around remote calls.
6. Enable health checks and distributed tracing.

## Achievements
- Cleaner microservice architecture.
- Easier operations and service governance.
- Improved reliability in distributed calls.

## Important Code Example

```java
@EnableDiscoveryClient // Registers service with discovery server
@SpringBootApplication
public class AccountServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AccountServiceApplication.class, args);
    }
}
```

```java
@FeignClient(name = "transfer-service") // Logical service name from discovery
public interface TransferClient {
    @GetMapping("/internal/transfers/{id}")
    TransferDto getTransfer(@PathVariable String id); // Calls discovered service instance
}
```
