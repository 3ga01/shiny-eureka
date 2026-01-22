package com.emmanuel.user_service.controller;

import com.emmanuel.user_service.utility.URI;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(URI.ADMIN_BASE_URI)
public class AdminController {

  @GetMapping("/dashboard")
  @RateLimiter(name = "userService")
  public String adminDashboard() {
    return "Admin access granted!";
  }
}
