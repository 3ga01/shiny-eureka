package com.emmanuel.user_service.unit.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.emmanuel.user_service.controller.AuthController;
import com.emmanuel.user_service.dto.request.LoginRequest;
import com.emmanuel.user_service.dto.request.SignUpRequest;
import com.emmanuel.user_service.dto.request.TokenRefreshRequest;
import com.emmanuel.user_service.dto.response.JwtResponse;
import com.emmanuel.user_service.dto.response.UserResponse;
import com.emmanuel.user_service.service.AuthService;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class AuthControllerUnitTest {

  @Mock private AuthService authService;

  @InjectMocks private AuthController authController;

  public AuthControllerUnitTest() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void signup_shouldCallAuthServiceAndReturnResponse() throws IOException {
    SignUpRequest request =
        new SignUpRequest("user", "user@mail.com", "Password1!", "First", "Last", "logoUrl");
    UserResponse expectedResponse =
        new UserResponse(1L, "user", "user@mail.com", null, null, "logoUrl");

    when(authService.signUp(request)).thenReturn(expectedResponse);

    UserResponse response = authController.signup(request);

    assertEquals(expectedResponse, response);
    verify(authService, times(1)).signUp(request);
  }

  @Test
  void signup_shouldReturnBadRequestForInvalidEmail() throws Exception {
    SignUpRequest request =
        new SignUpRequest(
            "user",
            "invalid-email", // invalid email
            "Password1!",
            "First",
            "Last",
            "logoUrl");
  }

  @Test
  void login_shouldCallAuthServiceAndReturnJwtResponse() {
    LoginRequest request = new LoginRequest("user", "Password1!");
    JwtResponse expectedResponse = new JwtResponse("access", "refresh");

    when(authService.login(request)).thenReturn(expectedResponse);

    JwtResponse response = authController.login(request);

    assertEquals(expectedResponse, response);
    verify(authService, times(1)).login(request);
  }

  @Test
  void refresh_shouldCallAuthServiceAndReturnJwtResponse() {
    TokenRefreshRequest request = new TokenRefreshRequest("refreshToken");
    JwtResponse expectedResponse = new JwtResponse("access", "refresh");

    when(authService.refreshToken(request)).thenReturn(expectedResponse);

    JwtResponse response = authController.refresh(request);

    assertEquals(expectedResponse, response);
    verify(authService, times(1)).refreshToken(request);
  }
}
