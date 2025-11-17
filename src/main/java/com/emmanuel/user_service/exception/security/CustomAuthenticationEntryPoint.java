package com.emmanuel.user_service.exception.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException)
      throws IOException {

    ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
    problemDetail.setTitle("Authentication Failed");
    problemDetail.setDetail("Invalid username or password");

    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    response.setContentType("application/json");
    // Serialize ProblemDetail to JSON
    response.getWriter().write(objectMapper.writeValueAsString(problemDetail));
  }
}
