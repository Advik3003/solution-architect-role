package com.interview.platform.account.service;

import java.time.Instant;
import java.util.Map;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class AccountQueryService {

    private final StringRedisTemplate redisTemplate;

    public AccountQueryService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Map<String, Object> getAccountV1(String accountId) {
        // Why this exists:
        // Cache-aside protects DB from hot-read traffic and improves p95 latency.
        // Interview talking point:
        // "I add Redis in read-heavy paths first, then tune TTL and invalidation strategy."
        // Interview point: cache-aside pattern with Redis.
        String key = "acc:v1:" + accountId;
        String cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            return Map.of("accountId", accountId, "status", "ACTIVE", "cache", "HIT", "raw", cached);
        }

        Map<String, Object> fromDb = Map.of(
            "accountId", accountId,
            "status", "ACTIVE",
            "cache", "MISS"
        );
        redisTemplate.opsForValue().set(key, "ACTIVE");
        return fromDb;
    }

    public Map<String, Object> getAccountV2(String accountId) {
        // Why this exists:
        // Versioned contract lets us evolve API shape without breaking old consumers.
        // Interview talking point:
        // "I keep v1 stable while introducing additive capabilities in v2."
        // V2 can expose additional fields while v1 stays unchanged.
        return Map.of(
            "accountId", accountId,
            "status", "ACTIVE",
            "accountType", "SAVINGS",
            "asOf", Instant.now().toString()
        );
    }
}
