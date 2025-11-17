package com.emmanuel.user_service.controller;

import com.emmanuel.user_service.dto.*;
import com.emmanuel.user_service.service.AuthService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/signup")
  @RateLimiter(name = "userService")
  @ResponseStatus(HttpStatus.CREATED)
  public CreateUserResponse signup(@RequestBody @Valid SignUpRequest request) {
    return authService.createUser(request);
  }

  @PostMapping("/login")
  public JwtResponse login(@RequestBody @Valid LoginRequest request) {
    return authService.login(request);
  }

  @PostMapping("/refresh")
  @RateLimiter(name = "userService")
  public JwtResponse refresh(@RequestBody @Valid TokenRefreshRequest request) {
    return authService.refreshToken(request);
  }
}
