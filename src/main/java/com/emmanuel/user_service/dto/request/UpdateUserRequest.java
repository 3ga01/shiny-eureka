package com.emmanuel.user_service.dto.request;

public record UpdateUserRequest(
    String username, String password, String firstName, String lastName, String logoUrl) {}
