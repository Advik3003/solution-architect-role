# System Design: ICICI-Type Banking Application

This document provides a system design blueprint for a digital banking platform similar to ICICI, covering frontend, backend (Spring Boot), data, security, cloud, scalability, and operational readiness.

## 1) System Goals

- Secure banking transactions with compliance-friendly architecture
- High availability for customer channels (web/mobile)
- Strong consistency for monetary operations
- Scalable read workloads for account summary and statements
- Full observability, auditability, and disaster recovery readiness

## 2) Core Functional Modules

- Authentication and user identity
- Customer profile and KYC
- Accounts and balances
- Beneficiary management
- Fund transfers and payment workflows
- Statements and transaction history
- Notifications and alerts
- Admin operations and audit dashboards

## 3) High-Level Architecture (C4: Container View)

### Channels
- Web frontend (React)
- Mobile app (Flutter/React Native)
- Admin web portal

### Edge and Access
- CDN + WAF
- Load balancer
- API Gateway (Spring Cloud Gateway)

### Core Service Layer (Spring Boot Microservices)
- Auth Service
- Customer Service
- Account Service
- Beneficiary Service
- Transfer Service
- Payment Integration Service
- Notification Service
- Audit Service
- Risk and Limits Service

### Data and Platform
- PostgreSQL/Oracle (transactional system of record)
- Redis (cache, OTP/session/rate limiting)
- Kafka/RabbitMQ (asynchronous event bus)
- Object storage (statements/KYC docs)
- OpenSearch/ELK (search and log analytics)

## 4) Key Request Flow (Example: Fund Transfer)

1. User authenticates with MFA.
2. Client calls `POST /transfers` through API Gateway.
3. Transfer Service validates payload + idempotency key.
4. Risk and Limits Service checks transfer limits and risk score.
5. Account Service verifies available balance.
6. Transaction entry is written using ACID transaction.
7. Transfer status event is published to message bus.
8. Notification Service sends SMS/email/push alert.
9. Audit Service stores immutable audit event.

Design notes:
- Debit/credit entries must be atomic.
- Idempotency prevents duplicate transfer processing.
- Async notifications must not block financial commit.

## 5) Data Design (Logical)

Primary entities:

- `customer`
- `account`
- `beneficiary`
- `transfer_request`
- `ledger_entry`
- `payment_reference`
- `audit_event`
- `notification_event`

Important rules:

- `ledger_entry` should be append-only (immutable record of financial movement).
- Every transfer operation should have a unique `idempotency_key`.
- Audit events must include actor, action, channel, timestamp, and correlation ID.

## 6) API Design Guidelines

- REST APIs with versioning: `/api/v1/...`
- OAuth2 token-based access with scope validation
- Consistent error model:
  - `errorCode`
  - `message`
  - `correlationId`
- Retry-safe operations use idempotency key header

Sample APIs:

- `POST /api/v1/auth/login`
- `GET /api/v1/accounts/{accountId}/summary`
- `POST /api/v1/beneficiaries`
- `POST /api/v1/transfers`
- `GET /api/v1/transfers/{transferId}/status`
- `GET /api/v1/accounts/{accountId}/statements`

## 7) Security Architecture

- TLS for all traffic (client-to-edge and service-to-service)
- OAuth2/OIDC + JWT for identity
- MFA for sensitive operations
- RBAC for admin and operations access
- Encryption at rest (database and object storage)
- Secrets managed via vault/secret manager
- WAF and API rate limiting
- Centralized audit logging and SIEM integration

## 8) Scalability Strategy

- Stateless Spring Boot services with horizontal auto-scaling
- Redis cache for hot reads (account summary/profile)
- Read replicas for statement/history-heavy queries
- Queue-driven async processing for notifications and non-critical workflows
- Partitioning strategy for high-volume ledger tables (time/account based)

## 9) Reliability and Resilience

- Circuit breaker and retry (Resilience4j)
- Timeout budgets for all downstream calls
- Dead-letter queues for failed async events
- Graceful degradation (e.g., delay notifications but complete transfer)
- Active-active across multiple AZs
- DR in secondary region (active-passive at first)

Target NFRs:
- Availability: 99.95%
- P95 latency for account summary: < 300 ms
- Transfer success reliability: >= 99.9% (excluding external outage)
- RPO: <= 5 minutes, RTO: <= 30 minutes

## 10) Observability and Operations

- Metrics: Prometheus/Grafana (or cloud equivalent)
- Logs: centralized structured logging
- Tracing: OpenTelemetry
- Dashboards:
  - transfer success/failure rate
  - latency by API and service
  - auth failures and suspicious behavior
- Alerts:
  - error rate spikes
  - payment integration timeout increase
  - unusual transfer pattern anomaly

## 11) DevOps and Release Management

- CI: build, unit test, integration test, SAST, dependency scan
- CD: canary/blue-green deployment with automated rollback
- Database migration: Flyway/Liquibase with controlled rollout
- Environment strategy: dev, test, staging, production
- Policy gates for production (security and compliance checks)

## 12) Trade-offs (Why + Pros + Cons)

### Microservices over monolith
- Why: better domain isolation and scale for banking functions
- Pros: team autonomy, targeted scaling, safer independent releases
- Cons: distributed complexity, cross-service consistency overhead

### Event-driven async integration
- Why: decouple notification/audit/risk side flows from main transactions
- Pros: resilience and extensibility
- Cons: eventual consistency and replay complexity

### Redis caching for read-heavy APIs
- Why: lower latency for frequent balance/profile lookups
- Pros: fast response, reduced DB load
- Cons: cache invalidation complexity

### Multi-AZ + DR region
- Why: business continuity for critical banking operations
- Pros: stronger resilience posture
- Cons: higher cost and operational management effort

## 13) Suggested Implementation Phases

### Phase 1: Foundation
- Auth, customer, account summary APIs
- CI/CD baseline, observability, audit scaffolding

### Phase 2: Core Transactions
- Beneficiary and transfer flows
- Risk checks, idempotency, resilient integration patterns

### Phase 3: Scale and Hardening
- Performance tuning, partitioning, DR drills
- Advanced fraud signals and operational automation

---

This design can be used as a baseline architecture document for engineering, security, and compliance teams before detailed low-level design and sprint planning.
