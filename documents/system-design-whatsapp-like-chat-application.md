# System Design: WhatsApp-Type Chat Application

This document defines a system design for a WhatsApp-like chat platform supporting real-time messaging, media sharing, groups, presence, and end-to-end security.

## 1) System Goals

- Real-time low-latency messaging at massive scale
- Reliable message delivery with offline support
- End-to-end secure communication
- Efficient media handling and storage
- High availability across regions

## 2) Core Functional Modules

- User registration and identity
- Contact sync and discovery
- One-to-one and group messaging
- Delivery/read receipts
- Presence (online/last seen/typing)
- Media messages (image/video/document/voice)
- Push notifications
- Admin abuse and moderation tooling

## 3) High-Level Architecture

### Client Layer
- Mobile-first apps (Android/iOS)
- Optional web client via QR-based device linking

### Realtime Gateway Layer
- WebSocket/MQTT gateway cluster for persistent connections
- Session routing and connection management

### Core Services
- Auth Service
- User Profile Service
- Contact Service
- Chat Service
- Group Service
- Presence Service
- Notification Service
- Media Service
- Abuse Detection Service

### Data and Messaging Platform
- Cassandra/DynamoDB for message storage (high write scale)
- Redis for presence/session state
- Kafka/Pulsar for event stream processing
- Object storage + CDN for media
- SQL store for metadata/admin controls

## 4) Key Request Flow (Message Send)

1. Sender posts encrypted message payload to chat gateway.
2. Gateway authenticates token and forwards to Chat Service.
3. Message is persisted in message store.
4. Delivery event is emitted to recipient connection if online.
5. If recipient offline, push notification is sent and message remains queued.
6. Delivery/read receipts are processed as state changes.

## 5) Implementation Approaches

### A) Persistent Realtime Connections
- Maintain long-lived socket connections per active device.
- Use connection routing table in Redis for fast lookup.
- Achieve: low-latency message delivery.

### B) Store-and-Forward Messaging
- Persist message before delivery attempt.
- Retry delivery on reconnect.
- Achieve: reliable delivery for offline users.

### C) Event-Driven Architecture
- Publish events for message sent/delivered/read.
- Use async workers for push notifications and analytics.
- Achieve: decoupled and scalable workflow processing.

### D) Media Offloading
- Upload media to object storage via signed URLs.
- Store only metadata + secure references in chat payload.
- Achieve: lower chat server load and efficient media distribution.

### E) Multi-Device Sync
- Maintain per-device session and cursor checkpoints.
- Sync undelivered messages on reconnect.
- Achieve: consistent user experience across devices.

## 6) What We Use, Why We Use It, and What We Achieve

### 1. Authentication
**What we use**
- OAuth2/JWT + device registration, optional Keycloak for IAM.
**Why**
- Secure device sessions and scalable API auth.
**What we achieve**
- Trusted identity across mobile/web clients.

### 2. Realtime Transport
**What we use**
- WebSocket (or MQTT for mobile efficiency).
**Why**
- Full-duplex communication with minimal latency.
**What we achieve**
- Near-instant message and presence updates.

### 3. Message Storage
**What we use**
- Cassandra/DynamoDB (partitioned by conversation/time).
**Why**
- Massive write throughput and horizontal scalability.
**What we achieve**
- Reliable storage at very high message volume.

### 4. Presence
**What we use**
- Redis for ephemeral connection/presence state.
**Why**
- Fast in-memory state updates.
**What we achieve**
- Responsive online/typing indicators.

### 5. Notifications
**What we use**
- Kafka/Pulsar + push providers (FCM/APNs).
**Why**
- Async fan-out for offline users.
**What we achieve**
- Timely message awareness without blocking core send flow.

### 6. Security
**What we use**
- End-to-end encryption protocol, TLS, key management, abuse controls.
**Why**
- Protect message confidentiality and platform integrity.
**What we achieve**
- User trust and privacy-first communication.

## 7) Non-Functional Targets (Sample)

- Message send-to-deliver P95: < 300 ms (online peers)
- Delivery reliability: >= 99.99%
- Availability: 99.99% for messaging path
- Media upload success: >= 99.9%

## 8) Pros and Cons

### Pros
- Real-time and reliable communication model
- Excellent scalability with event-driven architecture
- Strong privacy posture with encryption-first design

### Cons
- Operational complexity for socket infrastructure
- Encryption and key rotation workflows add implementation effort
- Global delivery performance depends on regional footprint

## 9) Suggested Rollout Phases

### Phase 1
- One-to-one messaging, auth, basic delivery/read receipts

### Phase 2
- Group chat, media sharing, push notifications, presence optimization

### Phase 3
- Multi-region optimization, abuse/fraud controls, advanced sync and reliability hardening
