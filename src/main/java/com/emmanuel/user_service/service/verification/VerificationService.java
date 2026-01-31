package com.emmanuel.user_service.service.verification;

import com.emmanuel.user_service.dto.request.VerificationRequest;
import com.emmanuel.user_service.dto.response.VerificationResponse;

public interface VerificationService {

  String generateToken();

  VerificationResponse saveVerificationToken(VerificationRequest verificationRequest);
}
