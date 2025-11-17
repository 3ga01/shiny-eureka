package com.emmanuel.user_service.controller;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

  @GetMapping("/dashboard")
  @RateLimiter(name = "userService")
  public String adminDashboard() {
    return "Admin access granted!";
  }
}
