package com.emmanuel.user_service.service;

import com.emmanuel.user_service.dto.*;

public interface AuthService {
  CreateUserResponse createUser(SignUpRequest signUpRequest);

  JwtResponse login(LoginRequest loginRequest);

  JwtResponse refreshToken(TokenRefreshRequest tokenRefreshRequest);
}
