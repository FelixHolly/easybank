package at.holly.easybankbackend.exception;

import at.holly.easybankbackend.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
        .message(ex.getMessage())
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

    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
        .message("An unexpected error occurred")
        .path(request.getRequestURI())
        .build();

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
  }
}
