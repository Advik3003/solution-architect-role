# Security Features

> Stack reference: `unified-stack-standard-springboot-aws.md`

## What It Is
Security features protect users, APIs, data, and infrastructure from unauthorized access and attacks.

## Why We Use It
- Prevent breaches and fraud.
- Meet regulatory and compliance obligations.
- Protect brand trust and business continuity.

## What We Use
- MFA, RBAC/ABAC, WAF, rate limiting
- TLS in transit, encryption at rest
- Secret management and key rotation
- SAST/DAST/dependency scanning

## How to Implement
1. Define security baseline and threat model.
2. Enforce secure authentication and authorization.
3. Protect APIs with WAF, limits, and bot controls.
4. Encrypt sensitive data and rotate keys regularly.
5. Add security testing in CI/CD.
6. Run periodic audits, penetration tests, and incident drills.

## Achievements
- Reduced attack surface.
- Better compliance readiness.
- Faster detection and response to threats.

## Important Code Example

```java
@Bean
SecurityFilterChain secure(HttpSecurity http) throws Exception {
    return http
        .headers(headers -> headers
            .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'self'")) // Blocks untrusted scripts
            .frameOptions(frame -> frame.deny())) // Prevents clickjacking
        .requiresChannel(channel -> channel.anyRequest().requiresSecure()) // Enforces HTTPS
        .build();
}
```

```java
public String maskPan(String pan) {
    // Keeps only last 4 digits visible for logs/audit views
    return "XXXX-XXXX-XXXX-" + pan.substring(pan.length() - 4);
}
```
