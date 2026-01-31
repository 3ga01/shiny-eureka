package com.emmanuel.user_service.service.verification;

import com.emmanuel.user_service.dto.request.VerificationRequest;
import com.emmanuel.user_service.dto.response.VerificationResponse;
import com.emmanuel.user_service.mapper.VerificationMapper;
import com.emmanuel.user_service.model.user.VerificationToken;
import com.emmanuel.user_service.repository.VerificationTokenRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VerificationServiceImpl implements VerificationService {
  private final VerificationTokenRepository verificationTokenRepository;
  private final VerificationMapper verificationMapper;

  @Override
  public String generateToken() {
    return UUID.randomUUID().toString();
  }

  @Override
  public VerificationResponse saveVerificationToken(VerificationRequest verificationRequest) {
    VerificationToken tokenEntity = verificationMapper.toEntity(verificationRequest);

    VerificationToken savedToken = verificationTokenRepository.save(tokenEntity);

    return verificationMapper.toDto(savedToken);
  }
}
