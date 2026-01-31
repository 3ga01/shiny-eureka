package com.emmanuel.user_service.dto.response;

import java.time.Instant;

public record VerificationResponse(
    Long id, Long userId, String token, Instant expiryDate, Instant createdAt, Instant updatedAt) {}
