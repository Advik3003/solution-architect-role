# Step-by-Step Implementation Guide

This guide explains exactly how the project was implemented and why each step matters.

## Step 1: Start with platform foundation

Implemented:
- `discovery-server` for service registration
- `gateway` as single API entry point

Why:
- Every microservice can scale independently without static URLs.
- Gateway centralizes security, routing, throttling, and correlation IDs.

## Step 2: Implement secure API service

Implemented in `account-service`:
- Spring Security as OAuth2 Resource Server
- JWT validation and role/scope-based endpoint protection
- API versioning controllers (`v1` and `v2`)

Why:
- Authentication proves identity.
- Authorization controls permissions.
- API versioning prevents client breakage during evolution.

## Step 3: Add persistence and schema versioning

Implemented:
- Flyway migration scripts under `db/migration`
- PostgreSQL as transactional store

Why:
- Controlled schema changes are mandatory in production systems.

## Step 4: Add performance layer

Implemented:
- Redis cache for account summary reads

Why:
- Reduces DB load and improves response latency.

## Step 5: Add async and integration messaging

Implemented:
- Kafka producer/consumer (event stream)
- RabbitMQ publisher (task queue)
- AWS SQS publisher (cloud queue integration)

Why:
- Async communication decouples services and handles traffic spikes.

## Step 6: Add resilience patterns

Implemented:
- Resilience4j circuit breaker + retry around payment dependency call

Why:
- Prevents cascading failures and improves system stability.

## Step 7: Add operational observability

Implemented:
- Correlation ID filter in gateway and service
- Structured logs with request tracing

Why:
- Makes debugging distributed calls faster in incidents.

## Step 8: Handle sensitive data safely

Implemented:
- Environment variable configuration
- AWS Secrets Manager integration class

Why:
- Prevents hardcoded secrets and improves compliance posture.

## Step 9: Add cloud storage pattern

Implemented:
- S3 pre-signed URL service

Why:
- Allows secure direct browser upload without exposing credentials.

## Step 10: Add deployment and scalability

Implemented:
- Kubernetes deployment, service, and HPA manifests
- GitHub Actions CI workflow

Why:
- Supports repeatable deployment, autoscaling, and release confidence.

## Best interview explanation pattern

For each component, answer in this order:
1. What problem it solves
2. Why this technology was selected
3. How it is implemented in this project
4. Trade-offs and alternatives

## Local Run Automation

Use:

- `./run-local.ps1`
- `./run-local.ps1 -NoBuild` (if jars are already built and you want faster startup)

Why script-based startup helps:
- Ensures infra starts before services.
- Preserves deterministic service order: discovery -> gateway -> account-service.
- Produces logs in one place (`logs/`) for easier troubleshooting and interview demos.
