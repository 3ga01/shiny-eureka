package com.emmanuel.user_service.dto.request;

import jakarta.validation.constraints.NotBlank;

public record TokenRefreshRequest(
    @NotBlank(message = "Refresh token must be provided") String refreshToken) {}
