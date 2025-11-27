package at.holly.easybankbackend.exception;

import at.holly.easybankbackend.dto.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Global exception handler for all controllers
 * Provides standardized error responses across the application
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  /**
   * Handle validation errors from @Valid annotation
   * Returns 400 Bad Request with detailed validation error messages
   *
   * @param ex the validation exception
   * @param request the HTTP request
   * @return error response with validation details
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationErrors(
      MethodArgumentNotValidException ex,
      HttpServletRequest request) {

    log.warn("Validation failed for request to {}: {}", request.getRequestURI(), ex.getBindingResult().getErrorCount() + " errors");

    // Extract field validation errors
    List<ErrorResponse.ValidationError> validationErrors = ex.getBindingResult()
        .getAllErrors()
        .stream()
        .map(error -> {
          String fieldName = ((FieldError) error).getField();
          String errorMessage = error.getDefaultMessage();
          return ErrorResponse.ValidationError.builder()
              .field(fieldName)
              .message(errorMessage)
              .build();
        })
        .collect(Collectors.toList());

    // Build error response
    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.BAD_REQUEST.value())
        .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
        .message("Validation failed for one or more fields")
        .path(request.getRequestURI())
        .validationErrors(validationErrors)
        .build();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  /**
   * Handle entity not found exceptions
   * Returns 404 Not Found
   *
   * @param ex the exception
   * @param request the HTTP request
   * @return error response
   */
  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleEntityNotFoundException(
      EntityNotFoundException ex,
      HttpServletRequest request) {

    log.warn("Entity not found for request to {}: {}", request.getRequestURI(), ex.getMessage());

    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.NOT_FOUND.value())
        .error(HttpStatus.NOT_FOUND.getReasonPhrase())
        .message(ex.getMessage() != null ? ex.getMessage() : "Resource not found")
        .path(request.getRequestURI())
        .build();

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }

  /**
   * Handle database constraint violations
   * Returns 409 Conflict
   *
   * @param ex the exception
   * @param request the HTTP request
   * @return error response
   */
  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
      DataIntegrityViolationException ex,
      HttpServletRequest request) {

    log.warn("Data integrity violation for request to {}: {}", request.getRequestURI(), ex.getMessage());

    String message = "Database constraint violation occurred. This may be due to duplicate values or invalid references.";
    if (ex.getMessage() != null && ex.getMessage().contains("Duplicate entry")) {
      message = "A record with this information already exists.";
    }

    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.CONFLICT.value())
        .error(HttpStatus.CONFLICT.getReasonPhrase())
        .message(message)
        .path(request.getRequestURI())
        .build();

    return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
  }

  /**
   * Handle access denied exceptions
   * Returns 403 Forbidden
   *
   * @param ex the exception
   * @param request the HTTP request
   * @return error response
   */
  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ErrorResponse> handleAccessDeniedException(
      AccessDeniedException ex,
      HttpServletRequest request) {

    log.warn("Access denied for request to {}: {}", request.getRequestURI(), ex.getMessage());

    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.FORBIDDEN.value())
        .error(HttpStatus.FORBIDDEN.getReasonPhrase())
        .message("You do not have permission to access this resource")
        .path(request.getRequestURI())
        .build();

    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
  }

  /**
   * Handle illegal argument exceptions
   * Returns 400 Bad Request
   *
   * @param ex the exception
   * @param request the HTTP request
   * @return error response
   */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
      IllegalArgumentException ex,
      HttpServletRequest request) {

    log.warn("Illegal argument for request to {}: {}", request.getRequestURI(), ex.getMessage());

    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.BAD_REQUEST.value())
        .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
        .message(ex.getMessage() != null ? ex.getMessage() : "Invalid argument provided")
        .path(request.getRequestURI())
        .build();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  /**
   * Handle malformed JSON request body
   * Returns 400 Bad Request
   *
   * @param ex the exception
   * @param request the HTTP request
   * @return error response
   */
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
      HttpMessageNotReadableException ex,
      HttpServletRequest request) {

    log.warn("Malformed JSON for request to {}: {}", request.getRequestURI(), ex.getMessage());

    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.BAD_REQUEST.value())
        .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
        .message("Malformed JSON request. Please check your request body format.")
        .path(request.getRequestURI())
        .build();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  /**
   * Handle general runtime exceptions
   * Returns 500 Internal Server Error
   *
   * @param ex the exception
   * @param request the HTTP request
   * @return error response
   */
  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<ErrorResponse> handleRuntimeException(
      RuntimeException ex,
      HttpServletRequest request) {

    // Log the actual error for debugging (not exposed to client)
    log.error("Runtime exception occurred for request to {}: {}", request.getRequestURI(), ex.getMessage(), ex);

    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
        .message("An unexpected error occurred. Please try again later.")
        .path(request.getRequestURI())
        .build();

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
  }

  /**
   * Handle generic exceptions
   * Returns 500 Internal Server Error
   *
   * @param ex the exception
   * @param request the HTTP request
   * @return error response
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenericException(
      Exception ex,
      HttpServletRequest request) {

    // Log the actual error for debugging (not exposed to client)
    log.error("Unexpected exception occurred for request to {}: {}", request.getRequestURI(), ex.getMessage(), ex);

    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
        .message("An unexpected error occurred. Please try again later.")
        .path(request.getRequestURI())
        .build();

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
  }
}
