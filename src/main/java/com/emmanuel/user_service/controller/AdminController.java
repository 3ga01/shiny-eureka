package com.emmanuel.user_service.controller;

import com.emmanuel.user_service.utility.URI;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(URI.ADMIN_BASE_URI)
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

  @GetMapping("/dashboard")
  @RateLimiter(name = "userService")
  public String adminDashboard() {
    return "Admin access granted!";
  }
}
