package com.emmanuel.user_service.dto;

import jakarta.validation.constraints.NotBlank;

public record TokenRefreshRequest(
    @NotBlank(message = "Refresh token must be provided") String refreshToken) {}
