package com.emmanuel.user_service.controller;

import com.emmanuel.user_service.dto.request.LoginRequest;
import com.emmanuel.user_service.dto.request.SignUpRequest;
import com.emmanuel.user_service.dto.request.TokenRefreshRequest;
import com.emmanuel.user_service.dto.response.JwtResponse;
import com.emmanuel.user_service.dto.response.UserResponse;
import com.emmanuel.user_service.service.AuthService;
import com.emmanuel.user_service.utility.URI;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(URI.AUTH_BASE_URI)
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/signup")
  @RateLimiter(name = "userService")
  @ResponseStatus(HttpStatus.CREATED)
  public UserResponse signup(@RequestBody @Valid SignUpRequest request)
      throws IOException, MessagingException {
    return authService.signUp(request);
  }

  @PostMapping("/login")
  @RateLimiter(name = "userService")
  public JwtResponse login(@RequestBody @Valid LoginRequest request) {
    return authService.login(request);
  }

  @PostMapping("/refresh")
  @RateLimiter(name = "userService")
  public JwtResponse refresh(@RequestBody @Valid TokenRefreshRequest request) {
    return authService.refreshToken(request);
  }

  @PostMapping("/activate")
  public UserResponse activateUserAccount(@RequestParam String token) {
    return authService.activateUserAccount(token);
  }
}
