package com.resume.job.tracker.exceptions;

import com.resume.job.tracker.dto.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException exception, HttpServletRequest request){
        String errorMessage = exception.getBindingResult().getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining(", "));
        ApiError apiError = new ApiError(
          400,
          "VALIDATION_FAILED",
                errorMessage,
                LocalDateTime.now(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleUserConflict(EmailAlreadyExistsException ex, HttpServletRequest request){
        String path = request.getRequestURI();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiError(409, "CONFLICT", ex.getMessage(), LocalDateTime.now(), path));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiError> handleUserNotFound(UserNotFoundException exception, HttpServletRequest request){
        String path = request.getRequestURI();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError(404, "NOT_FOUND", exception.getMessage(), LocalDateTime.now(), path));
    }

    @ExceptionHandler(ResumeNotFoundException.class)
    public ResponseEntity<ApiError> contentNotFound(ResumeNotFoundException exception, HttpServletRequest request){
        String path = request.getRequestURI();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError(404, "NOT_FOUND", exception.getMessage(), LocalDateTime.now(), path));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiError> handleInvalidCreds(InvalidCredentialsException ex, HttpServletRequest request){
        String path = request.getRequestURI();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiError(401, "UNAUTHORIZED", ex.getMessage(), LocalDateTime.now(), path));
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<ApiError> handleIllegalAccess(UnauthorizedAccessException ex, HttpServletRequest request){
        String path = request.getRequestURI();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiError(403, "FORBIDDEN", ex.getMessage(), LocalDateTime.now(), path));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> genericExceptions(Exception exception, HttpServletRequest request){
        log.error("Unexpected error occurred", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ApiError(500, "INTERNAL_SERVER_ERROR",
                        "An unexpected error occurred. Please try again later.",
                        LocalDateTime.now(),
                        request.getRequestURI())
        );
    }
}
