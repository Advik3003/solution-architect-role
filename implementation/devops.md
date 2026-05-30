# DevOps

> Stack reference: `unified-stack-standard-springboot-aws.md`

## What It Is
DevOps is the practice of integrating development, operations, and quality/security automation for faster and safer delivery.

## Why We Use It
- Faster release cycle.
- Higher deployment reliability.
- Better collaboration between teams.

## What We Use
- CI/CD pipelines (GitHub Actions/Jenkins/GitLab CI)
- Infrastructure as Code (Terraform)
- Automated testing and security scanning

## How to Implement
1. Create CI pipeline for build, test, lint, and scan.
2. Create CD pipeline with staged environments (dev/stage/prod).
3. Add deployment approvals and policy gates.
4. Automate infra provisioning with IaC.
5. Add rollback steps and post-deploy health validation.
6. Track DORA metrics for improvement.

## Achievements
- Continuous and predictable delivery.
- Lower manual errors.
- Faster feedback and incident recovery.

## Important Code Example

```yaml
name: ci-pipeline
on: [push]

jobs:
  build-test-scan:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4 # Pulls repository source
      - run: ./gradlew clean test # Runs unit tests
      - run: ./gradlew spotbugsMain # Static analysis check
      - run: docker build -t app:${{ github.sha }} . # Builds deployable image
```

```yaml
# Example deploy gate condition
if: success() && github.ref == 'refs/heads/main' # Deploy only from main after CI success
```
