package com.emmanuel.user_service.dto;

import java.time.Instant;

public record CreateUserResponse(
    Long id, String username, String email, Instant createdAt, Instant updatedAt) {}
