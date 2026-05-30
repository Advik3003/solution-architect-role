# System Design: Amazon-Type E-commerce Application

This document provides a practical system design blueprint for an Amazon-like e-commerce platform with frontend, backend, data, DevOps, cloud, and scaling strategy.

## 1) System Goals

- High availability during flash sales and peak traffic
- Fast product discovery and checkout performance
- Secure payments and fraud-aware transactions
- Scalable catalog, order, and delivery workflows
- Full observability and reliable operations

## 2) Core Functional Modules

- User authentication and profile
- Product catalog and inventory
- Search and recommendations
- Cart and checkout
- Payments and order management
- Shipment and delivery tracking
- Notifications and support workflows
- Admin and seller management

## 3) High-Level Architecture

### Frontend
- Customer web app: React/Next.js
- Mobile app: Flutter/React Native
- Seller/Admin portal: React

### Edge and Access
- CDN + WAF
- Load balancer
- API Gateway + optional BFF per client channel

### Core Services
- Identity Service
- Catalog Service
- Search Service
- Cart Service
- Checkout Service
- Payment Service
- Order Service
- Inventory Service
- Shipment Service
- Notification Service
- Recommendation Service

### Data and Platform
- PostgreSQL/MySQL (orders, payments, users)
- Document DB (product catalog flexibility)
- Redis (cart/session/hot reads)
- OpenSearch/Elasticsearch (search/filter/facet)
- Kafka/RabbitMQ (event-driven async processing)
- Object storage + CDN (media/assets)

## 4) Key Request Flow (Checkout)

1. User adds products to cart.
2. Checkout API validates stock and pricing.
3. Payment is authorized with gateway.
4. Order is created in transaction boundary.
5. Inventory is reserved/decremented.
6. Events are published (`order.created`, `payment.success`).
7. Notification and shipment workflows are triggered asynchronously.

## 5) Implementation Approaches

### A) Domain Microservices
- Split domains: catalog, cart, checkout, payment, order, inventory.
- Each service owns its database and API contract.
- Achieve: independent scaling and team autonomy.

### B) Event-Driven Workflows
- Use events for order lifecycle and notification fan-out.
- Add retry, DLQ, idempotent consumers.
- Achieve: resilience and loose coupling.

### C) Polyglot Data
- SQL for transactions, document DB for catalog, Redis for cache, OpenSearch for search.
- Achieve: performance by workload specialization.

### D) Caching and Performance
- Cache product pages, recommendations, and cart summaries.
- Use short TTL + invalidation on inventory/price changes.
- Achieve: lower latency and reduced DB load.

### E) Reliability
- Circuit breakers and timeouts around payment/shipment integrations.
- Canary releases for checkout/payment services.
- Achieve: controlled failure impact and safer releases.

## 6) What We Use, Why We Use It, and What We Achieve

### 1. Authentication
**What we use**
- OAuth2/OIDC + JWT, optional Keycloak/Auth0.
**Why**
- Standardized, scalable identity for web/mobile APIs.
**What we achieve**
- Secure customer login and role-aware access.

### 2. Search
**What we use**
- OpenSearch/Elasticsearch.
**Why**
- Full-text search, typo tolerance, and facet filters.
**What we achieve**
- Fast discovery and better conversion.

### 3. Cart
**What we use**
- Redis with TTL.
**Why**
- High-speed session/cart reads and writes.
**What we achieve**
- Smooth checkout experience under load.

### 4. Orders and Payments
**What we use**
- SQL transactions + idempotency keys.
**Why**
- Prevent duplicate order/payment operations.
**What we achieve**
- Financial correctness and easier reconciliation.

### 5. Recommendations
**What we use**
- Event streams + analytics/ML pipeline.
**Why**
- Learn from browsing and purchase behavior.
**What we achieve**
- Personalized experience and higher AOV.

### 6. DevOps
**What we use**
- CI/CD, Docker, Kubernetes, canary deployments.
**Why**
- Frequent safe releases.
**What we achieve**
- Faster delivery with lower production risk.

## 7) Non-Functional Targets (Sample)

- Availability: 99.95%
- P95 product page latency: < 2 seconds
- Checkout success rate: >= 99.9%
- RPO <= 10 minutes, RTO <= 30 minutes

## 8) Pros and Cons

### Pros
- Scales to large traffic and catalog growth
- Better feature velocity with service boundaries
- Performance tuned by workload-specific technologies

### Cons
- Higher operational complexity than monolith
- Strong observability and SRE maturity required
- Data consistency across services needs careful design

## 9) Suggested Rollout Phases

### Phase 1
- Identity, catalog, cart, basic checkout
- CI/CD + observability baseline

### Phase 2
- Payment, order, inventory events, shipment integration
- Search and caching optimization

### Phase 3
- Recommendation engine, multi-region DR, cost optimization
