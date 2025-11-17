package com.emmanuel.user_service.utility;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ErrorMessages {

  // Generic errors
  public static final String INTERNAL_SERVER_ERROR = "An unexpected error occurred";
  public static final String VALIDATION_FAILED = "Validation failed";
  public static final String BAD_REQUEST = "Bad request";

  // User-related errors
  public static final String USER_NOT_FOUND = "User not found";
  public static final String USER_ALREADY_EXISTS = "User already exists";
  public static final String EMAIL_ALREADY_EXISTS = "Email already exists";
  public static final String USERNAME_ALREADY_EXISTS = "Username already exists";

  // Authentication / Authorization
  public static final String INVALID_CREDENTIALS = "Invalid username or password";
  public static final String UNAUTHORIZED = "Unauthorized access";
  public static final String FORBIDDEN = "Forbidden";

  // Token-related
  public static final String INVALID_TOKEN = "Invalid token";
  public static final String EXPIRED_TOKEN = "Token has expired";
  public static final String TOKEN_REFRESH_FAILED = "Could not refresh token";
}
