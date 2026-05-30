# System Design: Facebook-Type Social Media Application

This document outlines a system design for a Facebook-like social media platform including feed, posts, comments, reactions, messaging, notifications, and ad-ready scalability foundations.

## 1) System Goals

- High-scale content publishing and feed consumption
- Personalized and low-latency news feed
- Strong social graph modeling
- Reliable notifications and engagement tracking
- Moderation, privacy, and compliance support

## 2) Core Functional Modules

- User authentication and profile
- Friend/follow graph management
- Post creation (text/image/video)
- News feed generation and ranking
- Likes, comments, shares
- Notifications and in-app alerts
- Messaging integration (basic)
- Content moderation and reporting

## 3) High-Level Architecture

### Frontend
- Web app (React/Next.js)
- Mobile app (Flutter/React Native)
- Admin/moderation portal

### Edge Layer
- CDN + WAF + API gateway
- Image/video optimization pipelines

### Core Services
- Auth Service
- Profile Service
- Social Graph Service
- Post Service
- Feed Service
- Reaction Service
- Comment Service
- Notification Service
- Moderation Service
- Media Service

### Data and Stream Platform
- Graph DB or graph model on scalable DB for relationships
- SQL/NoSQL for posts and metadata
- Redis for feed cache and hot objects
- Kafka/Pulsar for event stream (post, like, comment, share)
- Object storage + CDN for media
- Search index for discoverability

## 4) Key Request Flow (Feed Read)

1. User opens app and requests home feed.
2. Feed Service fetches precomputed/cached feed candidates.
3. Ranking logic orders posts based on relevance and recency.
4. Service hydrates metadata (reaction counts, author profile snippets).
5. Final feed response is returned and cached for short duration.

## 5) Implementation Approaches

### A) Fan-out Strategy
- Fan-out on write for average users; fan-out on read for celebrity/high-follower accounts.
- Achieve: balanced latency and infrastructure cost.

### B) Event-Driven Engagement Pipeline
- Publish post/reaction/comment/share events.
- Update counters, recommendations, and notifications asynchronously.
- Achieve: high throughput and decoupled growth.

### C) Feed Caching
- Cache ranked feed pages and popular content objects.
- Use short TTL + invalidation for high-churn entities.
- Achieve: fast feed load and reduced backend pressure.

### D) Media Pipeline
- Async processing for image/video resize/transcode.
- CDN distribution with region-aware delivery.
- Achieve: smooth media playback and global performance.

### E) Privacy and Moderation Layer
- Access control checks before feed rendering.
- Content policy pipeline for abuse detection.
- Achieve: safer platform and policy compliance.

## 6) What We Use, Why We Use It, and What We Achieve

### 1. Authentication
**What we use**
- OAuth2/OIDC + JWT + optional Keycloak/Auth0.
**Why**
- Standard secure identity and token lifecycle management.
**What we achieve**
- Trusted access and manageable session security.

### 2. Social Graph
**What we use**
- Graph database or graph-indexed relationship store.
**Why**
- Efficient traversal of friend/follow relationships.
**What we achieve**
- Faster mutual connections and relevance features.

### 3. Feed Generation
**What we use**
- Feed service + ranking pipeline + Redis cache.
**Why**
- Personalized response under high read volume.
**What we achieve**
- Better engagement and lower response latency.

### 4. Reactions and Comments
**What we use**
- Event stream + counter aggregation workers.
**Why**
- Avoid synchronous bottlenecks on high-traffic posts.
**What we achieve**
- Scalable engagement processing.

### 5. Media Storage and Delivery
**What we use**
- Object storage + CDN + async media processor.
**Why**
- Large media workloads need specialized distribution.
**What we achieve**
- Faster load times and reduced origin/server load.

### 6. Moderation
**What we use**
- Rule engine + ML-assisted moderation queues.
**Why**
- Detect spam, abuse, and policy violations at scale.
**What we achieve**
- Safer community and brand/regulatory protection.

## 7) Non-Functional Targets (Sample)

- Feed load P95: < 500 ms for first page
- Post publish success: >= 99.95%
- Availability: 99.95% core feed path
- Notification delay P95: < 2 seconds

## 8) Pros and Cons

### Pros
- Scales for heavy read/write social workloads
- Flexible architecture for personalization and new features
- Strong foundation for moderation and privacy controls

### Cons
- Feed ranking introduces algorithmic and operational complexity
- Media pipeline cost can grow quickly
- Privacy and compliance requirements need ongoing governance

## 9) Suggested Rollout Phases

### Phase 1
- Auth, profile, posting, basic feed, reactions/comments

### Phase 2
- Personalized ranking, notifications, media optimization, moderation pipeline

### Phase 3
- Advanced recommendations, global scaling, privacy hardening and cost optimization
