package com.projects.ratelimiter.controller;

import com.projects.ratelimiter.dto.RateLimitRequest;
import com.projects.ratelimiter.service.RateLimitService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rate-limit")
public class RateLimitController {

    private RateLimitService rateLimitService;
    public RateLimitController(RateLimitService rateLimitService)
    {
        this.rateLimitService=rateLimitService;
    }
    @PostMapping
    public ResponseEntity<String> postAPI(@Valid @RequestBody RateLimitRequest rateLimitRequest)
    {
        return rateLimitService.calculateLimit(rateLimitRequest)==-1?ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("U exceeded 5 hits per min"):ResponseEntity.status(HttpStatus.ACCEPTED).body("API hit accespted <5");

    }

}
