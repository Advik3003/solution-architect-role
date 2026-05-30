# Sensitive Data in Local and Cloud

> Stack reference: `unified-stack-standard-springboot-aws.md`

## What It Is
Sensitive data includes passwords, API keys, tokens, PII, financial data, and encryption keys.

## Why We Use Strict Controls
- Prevent data leaks and credential theft.
- Meet compliance standards.
- Reduce blast radius during incidents.

## What We Use
- Local: `.env` with encryption tools and restricted file permissions
- Cloud: Secrets Manager / Parameter Store / Vault
- KMS-based key management and rotation

## How to Implement (Local)
1. Never hardcode secrets in source code.
2. Keep secrets in local env files excluded by `.gitignore`.
3. Encrypt local secret files for shared environments.
4. Restrict developer machine access and use MFA.

## How to Implement (Cloud)
1. Store secrets only in managed secret stores.
2. Access secrets via IAM roles, not static credentials.
3. Rotate secrets/keys periodically.
4. Audit secret access logs and alert on anomalies.
5. Mask sensitive values in logs and monitoring tools.

## Achievements
- Better data protection and compliance posture.
- Safer secret lifecycle management.
- Lower risk of accidental exposure.

## Important Code Example

```bash
# Local: keep secrets outside git-tracked files
export DB_PASSWORD='local-dev-password' # Set in shell session, not in code
```

```java
public String fetchSecret() {
    // Retrieves secret at runtime from cloud secret manager
    GetSecretValueResponse response = secretsManagerClient.getSecretValue(
        GetSecretValueRequest.builder().secretId("prod/db/password").build());
    return response.secretString(); // App reads secret without hardcoding
}
```
