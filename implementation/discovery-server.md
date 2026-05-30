# Discovery Server

> Stack reference: `unified-stack-standard-springboot-aws.md`

## What It Is
A discovery server tracks running service instances so microservices can locate each other dynamically.

## Why We Use It
- Removes hardcoded service endpoints.
- Supports autoscaling and dynamic infrastructure.
- Improves resilience in service-to-service communication.

## What We Use
- Eureka / Consul / Kubernetes service discovery
- Heartbeats and health checks

## How to Implement
1. Deploy discovery service in HA mode.
2. Register each service instance on startup.
3. Configure health checks and heartbeat intervals.
4. Use client-side load balancing for service calls.
5. Remove unhealthy instances automatically.
6. Add monitoring for registration and availability.

## Achievements
- Dynamic and fault-tolerant service routing.
- Easier scaling and reduced manual config changes.

## Important Code Example

```java
@EnableEurekaServer // Turns this Spring Boot app into service registry
@SpringBootApplication
public class DiscoveryServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(DiscoveryServerApplication.class, args);
    }
}
```

```yaml
eureka:
  client:
    register-with-eureka: false # Registry itself does not self-register
    fetch-registry: false
  server:
    enable-self-preservation: true # Protects against mass eviction during network issues
```
