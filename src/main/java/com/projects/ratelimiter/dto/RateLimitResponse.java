package com.projects.ratelimiter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RateLimitResponse {
    private boolean allowed;
    private long remainingRequest;
    private long resetInSeconds;
}
//whatever u plan to send in as JSON RESPONSE , make a DTO of it .
