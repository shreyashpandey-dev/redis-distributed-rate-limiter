package com.projects.ratelimiter.exception;

import com.projects.ratelimiter.dto.RateLimitResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExxception {
    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<?> handleRateLimitExceeded(RateLimitExceededException exception)
    {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(
                (Map.of(
                        "allowed", false,
                        "message",exception.getMessage()
                ))
        );
    }


}
