# Redis

> Stack reference: `unified-stack-standard-springboot-aws.md`

## What It Is
Redis is an in-memory data store used for caching, session storage, and fast counters.

## Why We Use It
- Very low-latency reads and writes.
- Reduces load on primary database.
- Useful for rate limiting and session/token storage.

## What We Use
- Cache-aside pattern
- TTL-based keys
- Redis cluster/sentinel for HA

## How to Implement
1. Choose cacheable data (profiles, catalog, frequently read summaries).
2. Add cache-aside logic in services.
3. Define TTL per key type based on data freshness needs.
4. Invalidate cache on updates (write-through or explicit delete).
5. Add fallback to DB when cache misses.
6. Track hit ratio and eviction metrics.

## Achievements
- Faster APIs and better user experience.
- Lower DB CPU and query cost.
- Better scalability for read-heavy workloads.

## Important Code Example

```java
public AccountDto getAccountSummary(String accountId) {
    String key = "account:summary:" + accountId;
    AccountDto cached = redisTemplate.opsForValue().get(key);
    if (cached != null) {
        return cached; // Returns fast path from cache
    }

    AccountDto fresh = accountRepository.fetchSummary(accountId); // Fallback to DB
    redisTemplate.opsForValue().set(key, fresh, Duration.ofMinutes(5)); // TTL avoids stale long-lived cache
    return fresh;
}
```
