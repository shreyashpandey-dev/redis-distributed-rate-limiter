package com.projects.ratelimiter.service;
import com.projects.ratelimiter.dto.RateLimitRequest;
import com.projects.ratelimiter.dto.RateLimitResponse;
import com.projects.ratelimiter.exception.RateLimitExceededException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
@Service
public class RateLimitService {

    private final StringRedisTemplate redisTemplate;
    HashMap<String,Integer> rateLimitMap=new HashMap<>();
    private final int Window_size=60;
    private final int LIMIT=5;
    public RateLimitService(StringRedisTemplate redisTemplate)
    {
        this.redisTemplate = redisTemplate;
    }
    public RateLimitResponse calculateLimit(RateLimitRequest rateLimitRequest)
    {
        String key="rate:"+rateLimitRequest.getClientId();
        Long currentCnt=redisTemplate.opsForValue().increment(key);
        if(currentCnt==1)
        {
            redisTemplate.expire(key, Duration.ofSeconds(Window_size));
        }
        long ttl=redisTemplate.getExpire(key);
        long remainingReq=LIMIT-currentCnt;
        if(currentCnt>LIMIT)
        {
            throw new RateLimitExceededException("Rate Limit exceeded. Try again in "+ ttl+ " seconds");
        }
        return new RateLimitResponse(true,Math.max(remainingReq,0),ttl);
    }


}
