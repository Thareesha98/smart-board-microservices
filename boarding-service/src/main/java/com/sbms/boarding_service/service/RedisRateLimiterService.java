package com.sbms.boarding_service.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisRateLimiterService {

    private final StringRedisTemplate redisTemplate;

    public RedisRateLimiterService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean isAllowed(String key, int maxRequests, int windowSeconds) {

        Long current = redisTemplate.opsForValue().increment(key);

        if (current != null && current == 1) {
            redisTemplate.expire(key, Duration.ofSeconds(windowSeconds));
        }

        return current != null && current <= maxRequests;
    }
}