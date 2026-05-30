# Unified Stack Standard (Spring Boot + AWS)

This file defines the **single reference stack** used across all files in the `implementation` folder.

## 1) Standard Technology Baseline

- Language: Java 17 (or Java 21 if organization standard supports it)
- Backend: Spring Boot 3.x
- Security: Spring Security + OAuth2/OIDC + JWT + Keycloak
- Service Communication: REST + OpenFeign
- API Governance: Spring Cloud Gateway
- Service Discovery: Eureka (or Kubernetes native service discovery)
- Resilience: Resilience4j (circuit breaker, retry, timeout, bulkhead)
- Async Messaging: Kafka (event streaming), RabbitMQ/SQS (queue workloads)
- Cache: Redis
- Database: PostgreSQL (transactional), optional read replica strategy
- Object Storage: AWS S3
- Compute: Kubernetes (EKS) and selective Lambda for event tasks
- Infra: Terraform + Helm
- CI/CD: GitHub Actions (or Jenkins)
- Observability: OpenTelemetry + Prometheus/Grafana + centralized logs

## 2) How to Read Code Examples in This Folder

- All code snippets are written to match this baseline architecture.
- If multiple options are shown (example: Kafka and RabbitMQ), choose based on use case:
  - Kafka for event streams and analytics pipelines
  - RabbitMQ/SQS for task queue style processing
- AWS examples are default cloud reference; same design can be adapted to Azure/GCP.

## 3) Common Implementation Conventions

- APIs are versioned: `/api/v1/...`
- JWT access token is short-lived; refresh token rotation is mandatory
- All external calls include timeout + retry + circuit breaker
- Sensitive data is never hardcoded; secrets come from Secret Manager/Vault
- Every request carries `X-Correlation-Id` for distributed tracing
- Schema changes are migration-driven (Flyway/Liquibase)
- Deployments are progressive (rolling/canary/blue-green)

## 4) Delivery Order Recommendation

1. Security + API gateway + observability baseline
2. Core domain services and database migration setup
3. Async messaging and cache integration
4. Kubernetes autoscaling and release strategies
5. DR, cost optimization, and operational hardening

## 5) Outcome

Using one standard stack across all implementation topics improves:

- team consistency
- onboarding speed
- production reliability
- governance and audit readiness
