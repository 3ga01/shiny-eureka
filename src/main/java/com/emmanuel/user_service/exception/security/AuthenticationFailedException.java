package com.emmanuel.user_service.exception.security;

public class AuthenticationFailedException extends RuntimeException {
  public AuthenticationFailedException(String message) {
    super(message);
  }
}
