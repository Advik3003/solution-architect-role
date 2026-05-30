package com.interview.platform.account.auth;

import java.time.Duration;
import java.util.UUID;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenSessionService {

    private final StringRedisTemplate redisTemplate;

    public RefreshTokenSessionService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String createSession(String userId) {
        // Interview point: distributed login uses shared store for session tracking.
        String refreshTokenId = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set("session:" + refreshTokenId, userId, Duration.ofDays(7));
        return refreshTokenId;
    }

    public boolean isSessionValid(String refreshTokenId) {
        return redisTemplate.hasKey("session:" + refreshTokenId);
    }

    public void revokeSession(String refreshTokenId) {
        // Supports logout and compromised token invalidation.
        redisTemplate.delete("session:" + refreshTokenId);
    }
}
