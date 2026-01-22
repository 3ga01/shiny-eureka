package com.emmanuel.user_service.service;

import com.emmanuel.user_service.dto.request.LoginRequest;
import com.emmanuel.user_service.dto.request.SignUpRequest;
import com.emmanuel.user_service.dto.request.TokenRefreshRequest;
import com.emmanuel.user_service.dto.response.JwtResponse;
import com.emmanuel.user_service.dto.response.UserResponse;
import java.io.IOException;

public interface AuthService {
  UserResponse signUp(SignUpRequest signUpRequest) throws IOException;

  JwtResponse login(LoginRequest loginRequest);

  JwtResponse refreshToken(TokenRefreshRequest tokenRefreshRequest);
}
