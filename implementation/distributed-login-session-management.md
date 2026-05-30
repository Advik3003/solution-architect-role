# Distributed Login and Session Management

> Stack reference: `unified-stack-standard-springboot-aws.md`

## What It Is
Distributed login/session management keeps user authentication state consistent across multiple instances and regions.

## Why We Use It
- Support horizontal scaling.
- Avoid sticky-session dependency.
- Enable seamless user experience across devices.

## What We Use
- JWT for stateless access tokens
- Refresh token store in Redis/DB
- Centralized identity provider (Keycloak/Auth0)

## How to Implement
1. Use short-lived JWT access token for API calls.
2. Store refresh tokens securely with revocation support.
3. Maintain session/device metadata in shared store.
4. Implement token rotation on refresh.
5. Add logout-all-devices and token invalidation endpoints.
6. Detect abnormal session behavior (new location/device risk).

## Achievements
- Scalable and consistent login architecture.
- Better security and session control.
- Improved user trust and operational manageability.

## Important Code Example

```java
public String issueAccessToken(UserDetails user) {
    // Creates short-lived JWT for API authorization
    return Jwts.builder()
        .subject(user.getUsername())
        .claim("roles", user.getAuthorities()) // Embeds roles for authorization checks
        .expiration(Date.from(Instant.now().plusSeconds(900))) // 15-minute token
        .signWith(signingKey)
        .compact();
}
```

```java
public void revokeRefreshToken(String tokenId) {
    // Stores revoked token identifier in Redis to block reuse
    redisTemplate.opsForValue().set("revoked:" + tokenId, "1", Duration.ofDays(7));
}
```
