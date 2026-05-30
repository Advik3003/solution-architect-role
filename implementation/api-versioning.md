# API Versioning

> Stack reference: `unified-stack-standard-springboot-aws.md`

## What It Is
API versioning manages API changes without breaking existing clients.

## Why We Use It
- Preserve backward compatibility.
- Enable gradual feature evolution.
- Avoid forced client upgrades.

## What We Use
- URI versioning (`/api/v1`)
- Header/media type versioning (for advanced use cases)
- API deprecation and sunset policy

## How to Implement
1. Define versioning strategy early.
2. Keep old versions running during transition.
3. Introduce breaking changes only in new major versions.
4. Document change logs and migration guides.
5. Add contract tests for each supported version.
6. Track adoption and retire old versions with notice.

## Achievements
- Stable integrations for consumers.
- Safer API evolution across releases.

## Important Code Example

```java
@RestController
@RequestMapping("/api/v1/accounts") // v1 route keeps old clients stable
public class AccountControllerV1 {

    @GetMapping("/{id}")
    public AccountDto getAccount(@PathVariable String id) {
        // Returns schema expected by v1 consumers
        return new AccountDto(id, "ACTIVE");
    }
}

@RestController
@RequestMapping("/api/v2/accounts") // v2 introduces breaking response changes
public class AccountControllerV2 {

    @GetMapping("/{id}")
    public AccountV2Dto getAccount(@PathVariable String id) {
        // New version can add richer fields without breaking v1 clients
        return new AccountV2Dto(id, "ACTIVE", "SAVINGS");
    }
}
```
