# Authentication and Authorization

> Stack reference: `unified-stack-standard-springboot-aws.md`

## What It Is
- **Authentication** verifies user identity (who the user is).
- **Authorization** decides access rights (what the user can do).

## Why We Use It
- Protect business data and transactions.
- Enforce role-based access (admin, customer, support).
- Meet security and compliance requirements.

## What We Use
- OAuth2/OIDC for standardized auth flows.
- JWT for stateless API access tokens.
- Keycloak/Auth0/Okta for centralized identity.
- RBAC and ABAC for permission control.

## How to Implement
1. Add identity provider (Keycloak/Auth0) and create realms/clients.
2. Configure login flow (Authorization Code + PKCE for frontend apps).
3. Issue short-lived JWT access tokens and refresh tokens.
4. Validate JWT in API gateway and backend services.
5. Define roles/scopes and enforce with policy checks in APIs.
6. Add MFA for critical operations.

## Achievements
- Secure login across web/mobile/microservices.
- Centralized user and role management.
- Reduced unauthorized access risk.

## Important Code Example

```java
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable()) // Disable for stateless JWT APIs
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN") // Role-based authorization
                .requestMatchers("/api/v1/user/**").hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated())
            .oauth2ResourceServer(oauth -> oauth.jwt()) // Validates JWT from IdP
            .build();
    }
}
```

```java
@PreAuthorize("hasAuthority('SCOPE_transfer.write')") // Scope-level permission
@PostMapping("/api/v1/transfers")
public ResponseEntity<String> createTransfer() {
    // Only tokens with transfer.write scope can execute this API
    return ResponseEntity.ok("Transfer accepted");
}
```
