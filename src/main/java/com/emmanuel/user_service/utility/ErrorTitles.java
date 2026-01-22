package com.emmanuel.user_service.utility;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ErrorTitles {

  // Account related titles
  public static final String ACCOUNT_NOT_FOUND = "Account not found";
  public static final String ACCOUNT_ALREADY_EXISTS = "Account already exists";
  public static final String INVALID_CREDENTIALS = "Invalid credentials";
  public static final String UNAUTHORIZED = "Unauthorized";
  public static final String FORBIDDEN = "Forbidden";
  public static final String INVALID_TOKEN = "Invalid token";
  public static final String EXPIRED_TOKEN = "Expired token";
  public static final String TOKEN_REFRESH_FAILED = "Token refresh failed";
  public static final String INVALID_REFRESH_TOKEN = "Invalid refresh token";
  public static final String INVALID_USER_ID = "Invalid user id";
  public static final String USER_NOT_FOUND = "User not found";
  public static final String INVALID_ROLE = "Invalid role";
  public static final String INVALID_PERMISSION = "Invalid permission";

  // Validation related titles
  public static final String VALIDATION_FAILED = "Validation failed";
  public static final String INVALID_FIELDS = "Invalid fields";
  public static final String INVALID_DATE = "Invalid date";
  public static final String INVALID_DATE_FORMAT = "Invalid date format";
  public static final String INVALID_PHONE_NUMBER = "Invalid phone number";

  // Generic titles
  public static final String INTERNAL_SERVER_ERROR = "Internal server error";
  public static final String BAD_REQUEST = "Bad request";
  public static final String ACCESS_DENIED = "Access Denied";
  public static final String DUPLICATE_RESOURCE = "Duplicate Resource";
  public static final String ACCOUNT_LOCKED = "Account Locked";
  public static final String AUTH_FAILED = "Authentication Failed";
}
