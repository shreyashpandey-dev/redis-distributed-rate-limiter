package com.projects.ratelimiter.service;

import com.projects.ratelimiter.config.RedisConfig;
import com.projects.ratelimiter.dto.RateLimitRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
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
    public int calculateLimit(RateLimitRequest rateLimitRequest)
    {
        String key="rate:"+rateLimitRequest.getClientId();
        Long currentCnt=redisTemplate.opsForValue().increment(key);
        if(currentCnt==1)
        {
            redisTemplate.expire(key, Duration.ofSeconds(Window_size));
        }
        if(currentCnt>LIMIT)
            return -1;
        return 1;
    }


}
