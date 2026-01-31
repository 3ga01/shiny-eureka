package com.emmanuel.user_service.dto.request;

import com.emmanuel.user_service.model.user.User;
import java.time.Instant;
import lombok.Builder;

@Builder
public record VerificationRequest(User user, String token, Instant expiryDate) {}
