package com.projects.ratelimiter.service;
import com.projects.ratelimiter.config.RateLimitProperties;
import com.projects.ratelimiter.dto.RateLimitRequest;
import com.projects.ratelimiter.dto.RateLimitResponse;
import com.projects.ratelimiter.exception.RateLimitExceededException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
@Service
public class RateLimitService {

    private final StringRedisTemplate redisTemplate;
    private final RateLimitProperties props;

    public RateLimitService(StringRedisTemplate redisTemplate, RateLimitProperties props) {
        this.redisTemplate = redisTemplate;
        this.props = props;
    }

    public RateLimitResponse calculateLimit(RateLimitRequest rateLimitRequest) {
        String key = "rate:" + rateLimitRequest.getClientId();
        Long currentCnt = redisTemplate.opsForValue().increment(key);
        if (currentCnt == 1) {
            redisTemplate.expire(key, Duration.ofSeconds(props.getWindowSeconds()));
        }
        long ttl = redisTemplate.getExpire(key);
        long remainingReq = props.getLimit() - currentCnt;
        if (currentCnt > props.getLimit()) {
            throw new RateLimitExceededException("Rate Limit exceeded. Try again in " + ttl + " seconds");
        }
        return new RateLimitResponse(true, Math.max(remainingReq, 0), ttl);
    }
}
