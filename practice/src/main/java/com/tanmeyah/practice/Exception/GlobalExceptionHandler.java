package com.tanmeyah.practice.Exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /** From AppServiceImpl: NotFoundException (user.not.found, task.not.found, etc.) */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(NotFoundException ex, HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiErrorResponse.of(
                        HttpStatus.NOT_FOUND.value(),
                        HttpStatus.NOT_FOUND.getReasonPhrase(),
                        ex.getMessage(),
                        request.getRequestURI(),
                        null
                ));
    }

    /** From AppServiceImpl: ConflictException (email.already.exists, etc.) */
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiErrorResponse> handleConflict(ConflictException ex, HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiErrorResponse.of(
                        HttpStatus.CONFLICT.value(),
                        HttpStatus.CONFLICT.getReasonPhrase(),
                        ex.getMessage(),
                        request.getRequestURI(),
                        null
                ));
    }

    /** DTO validation (@Valid on request bodies) */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiErrorResponse.of(
                        HttpStatus.BAD_REQUEST.value(),
                        HttpStatus.BAD_REQUEST.getReasonPhrase(),
                        "Validation failed",
                        request.getRequestURI(),
                        Map.of("fieldErrors", fieldErrors)
                ));
    }

    /** Query/path parameter validation */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        Map<String, String> violations = new LinkedHashMap<>();
        for (ConstraintViolation<?> v : ex.getConstraintViolations()) {
            violations.put(String.valueOf(v.getPropertyPath()), v.getMessage());
        }
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiErrorResponse.of(
                        HttpStatus.BAD_REQUEST.value(),
                        HttpStatus.BAD_REQUEST.getReasonPhrase(),
                        "Validation failed",
                        request.getRequestURI(),
                        Map.of("violations", violations)
                ));
    }

    /** Malformed JSON */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiErrorResponse.of(
                        HttpStatus.BAD_REQUEST.value(),
                        HttpStatus.BAD_REQUEST.getReasonPhrase(),
                        "Malformed JSON request",
                        request.getRequestURI(),
                        Map.of("cause", ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage())
                ));
    }

    /** Wrong type for @PathVariable (e.g. /users/abc) */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiErrorResponse.of(
                        HttpStatus.BAD_REQUEST.value(),
                        HttpStatus.BAD_REQUEST.getReasonPhrase(),
                        "Invalid parameter type",
                        request.getRequestURI(),
                        Map.of(
                                "parameter", ex.getName(),
                                "value", String.valueOf(ex.getValue()),
                                "requiredType", ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : null
                        )
                ));
    }

    /** Login: wrong email/password (AppServiceImpl.login → AuthenticationManager) */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleBadCredentials(BadCredentialsException ex, HttpServletRequest request) {
        String message = messageSource.getMessage("login.failed", null, LocaleContextHolder.getLocale());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiErrorResponse.of(
                        HttpStatus.UNAUTHORIZED.value(),
                        HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                        message,
                        request.getRequestURI(),
                        null
                ));
    }

    /** Register as ADMIN without secret / wrong secret */
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiErrorResponse> handleForbidden(ForbiddenException ex, HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiErrorResponse.of(
                        HttpStatus.FORBIDDEN.value(),
                        HttpStatus.FORBIDDEN.getReasonPhrase(),
                        ex.getMessage(),
                        request.getRequestURI(),
                        null
                ));
    }

    /** AppController when JWT principal is missing or wrong type */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiErrorResponse> handleUnauthorized(UnauthorizedException ex, HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiErrorResponse.of(
                        HttpStatus.UNAUTHORIZED.value(),
                        HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                        ex.getMessage(),
                        request.getRequestURI(),
                        null
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiErrorResponse.of(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                        "Unexpected error",
                        request.getRequestURI(),
                        Map.of("cause", ex.getMessage() != null ? ex.getMessage() : ex.getClass().getSimpleName())
                ));
    }
}
