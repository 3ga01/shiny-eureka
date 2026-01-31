package com.emmanuel.user_service.dto.request;

import com.emmanuel.user_service.model.user.User;
import lombok.Builder;

@Builder
public record MailRequest(
    String to, String subject, String htmlFilePath, User user, String activationUrl) {}
