# Interview Mini Platform (Spring Boot + Cloud)

This is a small but practical project that demonstrates how to implement major system design topics commonly asked in Solution Architect and Senior Full-Stack interviews.

## What this project demonstrates

- Authentication and authorization (OAuth2 Resource Server + JWT)
- API versioning (`/api/v1`, `/api/v2`)
- API Gateway + distributed tracing header (`X-Correlation-Id`)
- Service discovery (Eureka)
- Redis cache
- Kafka events
- RabbitMQ queue messaging
- AWS SQS messaging
- Circuit breaker + retry (Resilience4j)
- Database versioning (Flyway SQL scripts)
- Sensitive secrets handling (env + AWS Secrets Manager)
- S3 pre-signed URL flow
- Kubernetes deployment + HPA (scalability)
- DevOps CI workflow

## Project structure

- `discovery-server/` - Eureka service registry
- `gateway/` - Spring Cloud Gateway
- `account-service/` - core API service with examples
- `docker-compose.yml` - local infra (Postgres, Redis, Kafka, RabbitMQ, LocalStack)
- `k8s/` - deployment and scaling manifests
- `docs/step-by-step-implementation.md` - implementation walkthrough
- `docs/how-to-run-and-test.md` - run/test checklist with expected results
- `run-local.ps1` - one-command local startup
- `stop-local.ps1` - one-command local shutdown

## Quick start

1. Start local dependencies:
   - `docker compose up -d`
2. Start services in order:
   - `discovery-server`
   - `gateway`
   - `account-service`
3. Call APIs through gateway:
   - `GET http://localhost:8080/api/v1/accounts/1001`
   - `GET http://localhost:8080/api/v2/accounts/1001`
4. Trigger transfer API (secured route example):
   - `POST http://localhost:8080/api/v1/transfers`

## Interview talking points

- Why API gateway centralizes security and observability
- Why discovery avoids hardcoded service endpoints
- Why versioning strategy prevents client breakage
- When to choose Kafka vs RabbitMQ vs SQS
- How circuit breaker prevents cascading failures
- How to secure secrets in local and cloud environments
- How autoscaling and HPA improve reliability under load
