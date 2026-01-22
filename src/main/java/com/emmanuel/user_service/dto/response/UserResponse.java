package com.emmanuel.user_service.dto.response;

import java.time.Instant;

public record UserResponse(
    Long id, String username, String email, Instant createdAt, Instant updatedAt, String logoUrl) {}
