package com.projects.ratelimiter.service;

import com.projects.ratelimiter.config.RateLimitProperties;
import com.projects.ratelimiter.dto.RateLimitRequest;
import com.projects.ratelimiter.dto.RateLimitResponse;
import com.projects.ratelimiter.exception.RateLimitExceededException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Set;
import java.util.UUID;

@Service
public class RateLimitService {

    private final StringRedisTemplate redisTemplate;
    private final RateLimitProperties props;

    public RateLimitService(StringRedisTemplate redisTemplate, RateLimitProperties props) {
        this.redisTemplate = redisTemplate;
        this.props = props;
    }

    public RateLimitResponse calculateLimit(RateLimitRequest request) {
        String key = "rate:" + request.getClientId();
        long nowMs = System.currentTimeMillis();
        long windowMs = props.getWindowSeconds() * 1000L;

        // Drop all entries that have slid outside the window
        redisTemplate.opsForZSet().removeRangeByScore(key, 0, nowMs - windowMs);

        // How many requests are in the current sliding window?
        Long count = redisTemplate.opsForZSet().zCard(key);
        long currentCount = count == null ? 0 : count;

        if (currentCount >= props.getLimit()) {
            long resetInSeconds = timeUntilOldestSlides(key, nowMs, windowMs);
            throw new RateLimitExceededException("Rate Limit exceeded. Try again in " + resetInSeconds + " seconds");
        }

        // Record this request (UUID member, timestamp score)
        redisTemplate.opsForZSet().add(key, UUID.randomUUID().toString(), nowMs);
        redisTemplate.expire(key, Duration.ofSeconds(props.getWindowSeconds()));

        long remaining = props.getLimit() - currentCount - 1;
        long resetInSeconds = timeUntilOldestSlides(key, nowMs, windowMs);
        return new RateLimitResponse(true, remaining, resetInSeconds);
    }

    // When will the oldest request in the window slide out, freeing a slot?
    private long timeUntilOldestSlides(String key, long nowMs, long windowMs) {
        Set<ZSetOperations.TypedTuple<String>> oldest = redisTemplate.opsForZSet()
                .rangeByScoreWithScores(key, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0, 1);
        if (oldest == null || oldest.isEmpty()) {
            return props.getWindowSeconds();
        }
        double oldestScore = oldest.iterator().next().getScore();
        return Math.max(1, (long) ((oldestScore + windowMs - nowMs) / 1000));
    }
}
