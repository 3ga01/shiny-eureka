package com.emmanuel.user_service.exception.handler;

import com.emmanuel.user_service.exception.DuplicateResourceException;
import com.emmanuel.user_service.exception.security.AuthenticationFailedException;
import com.emmanuel.user_service.utility.ErrorMessages;
import io.sentry.Sentry;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
  @ExceptionHandler(Exception.class)
  public ProblemDetail handleAllExceptions(Exception ex) {
    log.error("Unhandled exception: {}", ex.getMessage(), ex);

    Sentry.captureException(ex);

    ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
    problemDetail.setTitle(ErrorMessages.INTERNAL_SERVER_ERROR);
    problemDetail.setDetail(ex.getMessage());
    problemDetail.setType(URI.create("https://example.com/problems/internal-server-error"));

    return problemDetail;
  }

  @ExceptionHandler(ResponseStatusException.class)
  public ProblemDetail handleResponseStatusException(ResponseStatusException ex) {
    log.error("ResponseStatusException: {}", ex.getMessage(), ex);

    Sentry.captureException(ex);

    ProblemDetail problemDetail = ProblemDetail.forStatus(ex.getStatusCode());
    problemDetail.setTitle(ex.getReason());
    problemDetail.setDetail(ex.getMessage());
    problemDetail.setType(URI.create("https://example.com/problems/" + ex.getStatusCode().value()));

    return problemDetail;
  }

  @ExceptionHandler(org.springframework.security.authorization.AuthorizationDeniedException.class)
  public ProblemDetail handleAuthorizationDeniedException(AuthorizationDeniedException ex) {
    log.error("Access denied: {}", ex.getMessage(), ex);
    Sentry.captureException(ex);

    ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
    problemDetail.setTitle("Access Denied");
    problemDetail.setDetail(ex.getMessage());
    problemDetail.setType(URI.create("https://example.com/problems/access-denied"));

    return problemDetail;
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ProblemDetail handleValidationExceptions(MethodArgumentNotValidException ex) {
    log.error("Validation failed: {}", ex.getMessage(), ex);
    Sentry.captureException(ex);

    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult()
        .getFieldErrors()
        .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

    ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
    problemDetail.setTitle("Validation Failed");
    problemDetail.setDetail(errors.toString());
    problemDetail.setType(URI.create("https://example.com/problems/validation-failed"));

    return problemDetail;
  }

  @ExceptionHandler(DuplicateResourceException.class)
  public ProblemDetail handleDuplicateResource(DuplicateResourceException ex) {
    ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
    pd.setTitle("Duplicate Record");
    pd.setDetail(ex.getMessage());
    return pd;
  }

  @ExceptionHandler(UsernameNotFoundException.class)
  public ProblemDetail handleUsernameNotFoundException(UsernameNotFoundException ex) {
    log.warn("User not found: {}", ex.getMessage());
    Sentry.captureException(ex);

    ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
    problemDetail.setTitle("User Not Found");
    problemDetail.setDetail(ex.getMessage());
    problemDetail.setType(URI.create("https://example.com/problems/user-not-found"));

    return problemDetail;
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ProblemDetail handleBadCredentialsException(BadCredentialsException ex) {
    log.warn("Authentication failed: {}", ex.getMessage());
    Sentry.captureException(ex);

    ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
    problemDetail.setTitle("Authentication Failed");
    problemDetail.setDetail("Invalid username or password");
    problemDetail.setType(URI.create("https://example.com/problems/authentication-failed"));

    return problemDetail;
  }

  @ExceptionHandler(AuthenticationFailedException.class)
  public ProblemDetail handleAuthenticationFailed(AuthenticationFailedException ex) {
    log.warn("Authentication failed: {}", ex.getMessage());
    Sentry.captureException(ex);

    ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
    problemDetail.setTitle("Authentication Failed");
    problemDetail.setDetail(ex.getMessage());
    problemDetail.setType(URI.create("https://example.com/problems/authentication-failed"));

    return problemDetail;
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ProblemDetail handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
    log.error("Data integrity violation: {}", ex.getMessage(), ex);
    Sentry.captureException(ex);

    ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
    problemDetail.setTitle("Invalid Data");
    problemDetail.setDetail(
        "Missing required fields or invalid values. " + ex.getMostSpecificCause().getMessage());
    problemDetail.setType(URI.create("https://example.com/problems/invalid-data"));

    return problemDetail;
  }

  @ExceptionHandler(org.springframework.security.authentication.LockedException.class)
  public ProblemDetail handleLockedException(LockedException ex) {
    log.warn("User account locked: {}", ex.getMessage());
    Sentry.captureException(ex);

    ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.LOCKED);
    problemDetail.setTitle("Account Locked");
    problemDetail.setDetail("Your account is locked. Please contact support.");
    problemDetail.setType(URI.create("https://example.com/problems/account-locked"));

    return problemDetail;
  }
}
