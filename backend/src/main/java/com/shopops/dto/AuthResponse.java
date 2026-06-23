package com.shopops.dto;

public record AuthResponse(
        String token,
        UserResponse user
) {
}
