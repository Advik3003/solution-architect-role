# Database Versioning

> Stack reference: `unified-stack-standard-springboot-aws.md`

## What It Is
Database versioning tracks schema changes in a controlled, repeatable way.

## Why We Use It
- Keep environments consistent.
- Reduce migration-related production issues.
- Support safe rollback/roll-forward.

## What We Use
- Flyway or Liquibase migration scripts
- Forward-only migrations
- Backward-compatible schema change pattern

## How to Implement
1. Store migration scripts in version control.
2. Apply migrations automatically in CI/CD.
3. Use additive changes first (new columns/tables).
4. Deploy app code compatible with old and new schema.
5. Remove deprecated schema only after traffic migration.
6. Validate migrations in staging with production-like data.

## Achievements
- Predictable database releases.
- Lower schema drift across environments.
- Safer continuous delivery.

## Important Code Example

```sql
-- V3__add_account_type_column.sql
ALTER TABLE account
ADD COLUMN account_type VARCHAR(20) DEFAULT 'SAVINGS'; -- Additive, backward compatible change
```

```bash
# Runs pending Flyway migrations in deployment pipeline
flyway -url=jdbc:postgresql://db:5432/app \
  -user=app_user \
  -password=*** \
  migrate
```
