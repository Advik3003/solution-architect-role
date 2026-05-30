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

## 14) How to Implement Each Approach (Execution Guide)

This section explains exactly how to implement the major approaches used in this system design.

### A) Implementing Microservices by Domain

1. Start with domain decomposition:
   - Identity
   - Customer
   - Account
   - Transfers
   - Payments
   - Notifications
2. Define service boundaries and ownership:
   - One team owns one service and its database schema.
   - Avoid direct DB access across services.
3. Create Spring Boot service template:
   - Common logging, error handling, tracing, health checks, security filters.
4. Introduce API Gateway routes:
   - Route per service with auth and rate-limit policies.
5. Add contract-first APIs:
   - OpenAPI specs, versioning rules, and backward compatibility checks.
6. Rollout:
   - Start with 2-3 core services (Auth, Account, Transfer), then expand.

### B) Implementing Event-Driven Integration

1. Identify async events:
   - `transfer.initiated`, `transfer.completed`, `beneficiary.added`, `kyc.updated`.
2. Define event schema:
   - `eventId`, `eventType`, `timestamp`, `correlationId`, `payload`, `version`.
3. Use outbox pattern:
   - Write transaction + event in same DB transaction, then publish safely.
4. Add consumers:
   - Notification Service, Audit Service, Analytics pipelines.
5. Add reliability controls:
   - Retry policy, dead-letter queue, idempotent consumer logic.
6. Observe and govern:
   - Monitor lag, failed events, replay metrics.

### C) Implementing Strongly Consistent Money Movement

1. Use ledger-centric design:
   - Never update balance directly without ledger entry.
2. Use ACID transaction for debit/credit:
   - Validate balance, write ledger entries, update transfer status atomically.
3. Add idempotency:
   - Require `Idempotency-Key` for transfer and payment APIs.
4. Concurrency control:
   - Use row-level locks or optimistic locking on account records.
5. Reconciliation:
   - Build daily and near-real-time reconciliation jobs for internal and partner settlement.

### D) Implementing Redis Caching Safely

1. Select cache candidates:
   - Account summary (read-heavy), profile data, static reference data.
2. Choose cache pattern:
   - Cache-aside for most reads.
3. TTL and invalidation policy:
   - Short TTL for account balance.
   - Explicit invalidation after transfer completion.
4. Protect against stale reads:
   - For sensitive flows (checkout equivalent: transfer confirmation), read from source of truth.
5. Monitor cache quality:
   - Hit ratio, stale read incidents, key eviction frequency.

### E) Implementing Security Controls

1. Identity:
   - Implement OAuth2/OIDC with short-lived JWT and refresh token policy.
2. MFA:
   - Enforce MFA for login from new device and high-value transfers.
3. Authorization:
   - RBAC for customer/admin roles, ABAC for risk-sensitive actions.
4. Data protection:
   - TLS everywhere, encrypted DB columns for sensitive fields, key rotation.
5. Secret management:
   - Move credentials/API keys to vault; remove secrets from code and CI variables.
6. Security testing:
   - SAST, DAST, dependency scans, periodic penetration testing.

### F) Implementing Observability and SRE Operations

1. Standardize telemetry:
   - Correlation ID in every request/response and event.
2. Logs:
   - Structured JSON logs with PII masking.
3. Metrics:
   - Golden signals: latency, traffic, errors, saturation.
4. Tracing:
   - OpenTelemetry instrumentation across gateway and services.
5. Alerting:
   - SLO-based alerts (not only CPU/memory), with runbooks for each alert.
6. Operational readiness:
   - On-call rotation, incident response workflow, postmortem template.

### G) Implementing DevOps and Release Strategy

1. Build pipeline:
   - Compile, test, code quality, security scans, artifact signing.
2. Environment promotion:
   - Dev -> Test -> Staging -> Production with approval gates.
3. Deployment model:
   - Canary for transfer/payment services; blue-green for low-risk services.
4. DB migration discipline:
   - Backward-compatible schema changes first, then code switch.
5. Rollback automation:
   - Trigger rollback on elevated failure rate or latency SLO breach.

### H) Implementing Multi-AZ and DR

1. Multi-AZ baseline:
   - Deploy service replicas across at least 2-3 AZs.
2. Database HA:
   - Primary + synchronous standby in same region.
3. DR region:
   - Async replication and warm standby services in secondary region.
4. DR procedures:
   - Define failover checklist, ownership, and communication protocol.
5. Drill cadence:
   - Run quarterly DR drills and publish RTO/RPO achievement reports.

### I) Recommended Order of Execution

1. Security baseline + CI/CD + observability first
2. Core APIs (Auth, Account, Transfer) with ACID and idempotency
3. Event bus and non-critical async consumers
4. Cache and performance optimization
5. DR and advanced hardening

## 15) What We Use, Why We Use It, and What We Achieve

This section explains each major component in simple business + technical language.

### 1. User Authentication and Authorization

**What it does**
- Verifies user identity (who is logging in).
- Controls permissions (what user/admin is allowed to do).

**What we use**
- **OAuth2/OIDC** for standardized login and token flows.
- **JWT** as access token for stateless API authorization.
- **Keycloak** (or similar IAM like Auth0/Okta) as centralized identity server.
- **MFA** for high-risk actions (new device login, high-value transfer).

**What we achieve**
- Secure and scalable login across web/mobile/backend.
- Single sign-on and centralized policy management.
- Lower risk of account takeover and unauthorized transactions.

**When to choose what**
- Use **JWT + OAuth2** when building modern API-first apps.
- Use **Keycloak** when you want self-hosted enterprise IAM with RBAC, realms, MFA, and audit support.

### 2. API Gateway

**What it does**
- Single entry point for all frontend requests.

**What we use**
- Spring Cloud Gateway (or Kong/NGINX API Gateway).

**What we achieve**
- Centralized authentication, rate limiting, routing, and API security.
- Cleaner backend services because cross-cutting logic is moved to gateway.

### 3. Microservices (Spring Boot)

**What it does**
- Splits banking domains into independent services.

**What we use**
- Spring Boot services by domain (Auth, Account, Transfer, Payment, Notification, Audit).

**What we achieve**
- Independent scaling, safer deployments, and better team ownership.
- Faster development for separate product tracks.

### 4. Database + Ledger Model

**What it does**
- Stores customer/account/transaction data with financial correctness.

**What we use**
- PostgreSQL/Oracle for ACID transactions.
- Append-only `ledger_entry` for immutable money movement records.

**What we achieve**
- Accurate balances, traceable transaction history, and compliance-friendly auditability.
- Safer reconciliation and dispute handling.

### 5. Redis Cache

**What it does**
- Serves frequently requested data quickly.

**What we use**
- Redis for account summary cache, sessions, OTP throttling.

**What we achieve**
- Lower API latency and reduced database load.
- Better customer experience during traffic peaks.

### 6. Event Bus (Kafka/RabbitMQ)

**What it does**
- Handles asynchronous workflows after core transaction completion.

**What we use**
- Kafka or RabbitMQ with retry + dead-letter queue.

**What we achieve**
- Loose coupling between services (transfer, notification, audit, analytics).
- Better resilience; non-critical tasks do not block critical transfer commit.

### 7. Security Controls

**What it does**
- Protects users, transactions, and sensitive data.

**What we use**
- TLS in transit, AES encryption at rest, vault for secrets, WAF, rate limits, SIEM integration.

**What we achieve**
- Strong defense-in-depth against fraud, abuse, and data leaks.
- Better audit and regulatory readiness.

### 8. Observability

**What it does**
- Gives real-time visibility into service health and business-critical flows.

**What we use**
- Logs (ELK/OpenSearch), metrics (Prometheus/Grafana), tracing (OpenTelemetry).

**What we achieve**
- Faster issue detection and root cause analysis.
- Lower downtime and better production reliability.

### 9. DevOps and CI/CD

**What it does**
- Automates build, test, security checks, and releases.

**What we use**
- GitHub Actions/Jenkins, Docker, Kubernetes, Flyway/Liquibase, canary/blue-green rollout.

**What we achieve**
- Faster and safer release cycles.
- Reduced manual error and controlled production changes.

### 10. Cloud + DR

**What it does**
- Ensures service continuity during failures.

**What we use**
- Multi-AZ deployment, managed databases, DR region with replication and failover drills.

**What we achieve**
- High availability and faster recovery from outages.
- Confidence for critical banking SLAs and customer trust.

### 11. Quick Mapping: Requirement -> Technology -> Outcome

- **Secure login** -> OAuth2 + JWT + Keycloak + MFA -> trusted access and reduced fraud
- **Fast APIs** -> Redis + autoscaling -> lower latency under high load
- **Reliable transfers** -> ACID + idempotency + ledger -> transaction correctness
- **Audit compliance** -> immutable logs + audit service -> traceability and governance
- **High availability** -> multi-AZ + DR + health checks -> business continuity

---

This design can be used as a baseline architecture document for engineering, security, and compliance teams before detailed low-level design and sprint planning.
