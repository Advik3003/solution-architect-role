# How to Run and Test the Interview Mini Platform

This guide explains how to run the project and validate that major architecture topics are working.

## 1) Prerequisites

- Java 21
- Maven 3.9+
- Docker Desktop
- PowerShell (Windows)

## 2) Start Everything

From `interview-mini-platform`:

```powershell
./run-local.ps1
```

Fast restart (skip build):

```powershell
./run-local.ps1 -NoBuild
```

## 3) Verify Startup

### Check core URLs

- Eureka UI: `http://localhost:8761`
- Gateway health (if exposed): `http://localhost:8080/actuator/health`
- Account service health: `http://localhost:8081/actuator/health`

### Check logs

Logs are created in:

- `logs/discovery-server.out.log`
- `logs/gateway.out.log`
- `logs/account-service.out.log`

## 4) API Testing (Versioning + Security)

### 4.1 Test security baseline

Without JWT token (expected unauthorized for protected APIs):

```powershell
curl http://localhost:8080/api/v1/accounts/1001
```

Expected:
- HTTP `401 Unauthorized`

Why this test matters:
- Confirms authentication is enforced globally.

### 4.2 Test actuator endpoint (public for probes)

```powershell
curl http://localhost:8081/actuator/health
```

Expected:
- HTTP `200`
- JSON with `"status":"UP"` (or similar)

Why this test matters:
- Confirms readiness/liveness probes for Kubernetes and monitoring.

### 4.3 Test transfer endpoint authorization

```powershell
curl -X POST http://localhost:8080/api/v1/transfers `
  -H "Content-Type: application/json" `
  -H "Idempotency-Key: demo-123" `
  -d '{"from":"A1","to":"A2","amount":1200}'
```

Expected without token:
- HTTP `401`

Expected with valid token containing scope `transfer.write`:
- HTTP `200`
- Response with `status=ACCEPTED` and returned idempotency key

Why this test matters:
- Demonstrates scope-based authorization and idempotency design.

## 5) Messaging Testing (Kafka / RabbitMQ / SQS)

After successful transfer call (with valid token), verify:

- Kafka consumer logs show consumed event:
  - `Kafka event consumed: ...`
- Analytics processor logs show stream processing:
  - `Analytics processor received: ...`

For RabbitMQ and SQS:
- Confirm publisher methods are wired and can be triggered from controller/service extensions.
- Check broker dashboards:
  - RabbitMQ UI: `http://localhost:15672` (if enabled by docker compose)
  - LocalStack endpoint: `http://localhost:4566`

## 6) Redis Cache Testing

Use repeated calls to same account endpoint (with valid token):

- First call expected cache miss path.
- Later calls expected cache hit path from Redis.

Interview talking point:
- Explain cache-aside and TTL strategy.

## 7) Database Versioning Testing (Flyway)

On startup, check account-service logs for Flyway:

- Migration `V1__create_account_table.sql`
- Migration `V2__add_account_type_column.sql`

Why this test matters:
- Proves controlled, versioned schema evolution.

## 8) Resilience Testing (Circuit Breaker)

`PaymentClient` points to a demo external URL (`payment-provider.local`) likely unavailable.

Expected behavior:
- Fallback response path used instead of cascading failure.

Interview talking point:
- Show that degraded mode is intentional and controlled.

## 9) Kubernetes Manifest Validation

Files:

- `k8s/account-service-deployment.yaml`
- `k8s/account-service-service.yaml`
- `k8s/account-service-hpa.yaml`

Validation commands:

```powershell
kubectl apply --dry-run=client -f k8s/account-service-deployment.yaml
kubectl apply --dry-run=client -f k8s/account-service-service.yaml
kubectl apply --dry-run=client -f k8s/account-service-hpa.yaml
```

## 10) Stop Everything

```powershell
./stop-local.ps1
```

What this does:
- Stops Spring Boot processes started by `run-local.ps1`
- Runs `docker compose down` for infra cleanup

## 11) Quick Troubleshooting

- Port conflict:
  - Check if `8080`, `8081`, `8761`, `5432`, `6379`, `9092`, `5672`, `4566` are free.
- Docker not running:
  - Start Docker Desktop first.
- Maven build failure:
  - Re-run with verbose logs: `mvn -X ...`
- Auth failures:
  - Expected without valid JWT issuer/token setup.

---

Use this guide during interviews to demonstrate not only architecture decisions, but also operational validation and reproducibility.
