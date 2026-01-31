package com.emmanuel.user_service.exception.handler;

import com.emmanuel.user_service.exception.DuplicateResourceException;
import com.emmanuel.user_service.exception.security.AuthenticationFailedException;
import com.emmanuel.user_service.utility.ErrorMessages;
import com.emmanuel.user_service.utility.ErrorTitles;
import io.sentry.Sentry;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
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
    log.error("{}: {}", ErrorTitles.INTERNAL_SERVER_ERROR, ex.getMessage(), ex);
    Sentry.captureException(ex);

    ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
    problemDetail.setTitle(ErrorTitles.INTERNAL_SERVER_ERROR);
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
    log.error("{}: {}", ErrorTitles.ACCESS_DENIED, ex.getMessage(), ex);
    Sentry.captureException(ex);

    ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
    problemDetail.setTitle(ErrorTitles.ACCESS_DENIED);
    problemDetail.setDetail(ex.getMessage());
    problemDetail.setType(URI.create("https://example.com/problems/access-denied"));

    return problemDetail;
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ProblemDetail handleValidationExceptions(MethodArgumentNotValidException ex) {
    log.error("{}}: {}", ErrorTitles.VALIDATION_FAILED, ex.getMessage(), ex);
    Sentry.captureException(ex);

    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult()
        .getFieldErrors()
        .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

    ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
    problemDetail.setTitle(ErrorTitles.VALIDATION_FAILED);
    problemDetail.setDetail(errors.toString());
    problemDetail.setType(URI.create("https://example.com/problems/validation-failed"));

    return problemDetail;
  }

  @ExceptionHandler(DuplicateResourceException.class)
  public ProblemDetail handleDuplicateResource(DuplicateResourceException ex) {
    ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
    pd.setTitle(ErrorTitles.DUPLICATE_RESOURCE);
    pd.setDetail(ex.getMessage());
    return pd;
  }

  @ExceptionHandler(UsernameNotFoundException.class)
  public ProblemDetail handleUsernameNotFoundException(UsernameNotFoundException ex) {
    log.warn("{}: {}", ErrorTitles.USER_NOT_FOUND, ex.getMessage());
    Sentry.captureException(ex);

    ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
    problemDetail.setTitle(ErrorTitles.USER_NOT_FOUND);
    problemDetail.setDetail(ex.getMessage());
    problemDetail.setType(URI.create("https://example.com/problems/user-not-found"));

    return problemDetail;
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ProblemDetail handleBadCredentialsException(BadCredentialsException ex) {
    log.warn("{}: {}", ErrorTitles.AUTH_FAILED, ex.getMessage());
    Sentry.captureException(ex);

    ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
    problemDetail.setTitle(ErrorTitles.AUTH_FAILED);
    problemDetail.setDetail(ErrorMessages.INVALID_CREDENTIALS);
    problemDetail.setType(URI.create("https://example.com/problems/authentication-failed"));

    return problemDetail;
  }

  @ExceptionHandler(AuthenticationFailedException.class)
  public ProblemDetail handleAuthenticationFailed(AuthenticationFailedException ex) {
    log.warn("{}: {}", ErrorTitles.AUTH_FAILED, ex.getMessage());
    Sentry.captureException(ex);

    ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
    problemDetail.setTitle(ErrorTitles.AUTH_FAILED);
    problemDetail.setDetail(ex.getMessage());
    problemDetail.setType(URI.create("https://example.com/problems/authentication-failed"));

    return problemDetail;
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ProblemDetail handleDataIntegrityViolationException(DataIntegrityViolationException ex) {

    log.error(ErrorTitles.INVALID_FIELDS, ex);
    Sentry.captureException(ex);

    String detail = resolveDetailMessage(ex);

    ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

    problemDetail.setTitle(ErrorTitles.INVALID_FIELDS);
    problemDetail.setDetail(detail);
    problemDetail.setType(URI.create("https://example.com/problems/invalid-data"));

    return problemDetail;
  }

  @ExceptionHandler(org.springframework.security.authentication.LockedException.class)
  public ProblemDetail handleLockedException(LockedException ex) {
    log.warn("{}: {}", ErrorTitles.ACCOUNT_LOCKED, ex.getMessage());
    Sentry.captureException(ex);

    ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.LOCKED);
    problemDetail.setTitle(ErrorTitles.ACCOUNT_LOCKED);
    problemDetail.setDetail(ErrorMessages.ACCOUNT_LOCKED);
    problemDetail.setType(URI.create("https://example.com/problems/account-locked"));

    return problemDetail;
  }

  @ExceptionHandler(DisabledException.class)
  public ProblemDetail handleDisabledException(DisabledException ex) {
    log.warn("{}: {}", ErrorTitles.ACCOUNT_DISABLED, ex.getMessage());
    Sentry.captureException(ex);

    ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
    problemDetail.setTitle(ErrorTitles.ACCOUNT_DISABLED);
    problemDetail.setDetail(ErrorMessages.ACCOUNT_DISABLED);
    problemDetail.setType(URI.create("https://example.com/problems/account-disabled"));

    return problemDetail;
  }

  private String resolveDetailMessage(DataIntegrityViolationException ex) {

    Throwable cause = ex;

    while (cause != null) {

      if (cause instanceof org.hibernate.exception.ConstraintViolationException cve) {

        String constraint = cve.getConstraintName();

        if ("users_username_key".equals(constraint)) {
          return ErrorMessages.USERNAME_ALREADY_EXISTS;
        }

        if ("users_email_key".equals(constraint)) {
          return ErrorMessages.EMAIL_ALREADY_EXISTS;
        }
      }

      cause = cause.getCause();
    }

    return ErrorMessages.INVALID_FIELDS;
  }
}
