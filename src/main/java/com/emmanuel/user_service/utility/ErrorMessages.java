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

  // Generic Validation Messages
  public static final String REQUIRED_FIELD = "This field is required";
  public static final String INVALID_EMAIL = "Invalid email address";
  public static final String INVALID_PASSWORD = "";
  public static final String INVALID_USERNAME = "";
  public static final String INVALID_FIRST_NAME = "";
  public static final String INVALID_LAST_NAME = "";
  public static final String INVALID_LOGO_URL = "";
  public static final String INVALID_ROLE = "";
  public static final String INVALID_USER_ID = "";
  public static final String INVALID_DATE = "";
  public static final String INVALID_DATE_FORMAT = "";
  public static final String INVALID_PHONE_NUMBER = "";
  public static final String INVALID_COUNTRY_CODE = "";
  public static final String INVALID_STATE = "";
  public static final String INVALID_CITY = "";
  public static final String INVALID_STREET_ADDRESS = "";
  public static final String INVALID_ZIP_CODE = "";
  public static final String INVALID_FIELDS = "Missing required fields or invalid values.";
  public static final String ACCOUNT_LOCKED = "Your account is locked. Please contact support.";
}
