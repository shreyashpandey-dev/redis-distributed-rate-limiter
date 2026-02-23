package com.projects.ratelimiter.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
@Data
public class RateLimitRequest {

    @NotBlank(message="Client id cannot be null")
    private String clientId;
}
