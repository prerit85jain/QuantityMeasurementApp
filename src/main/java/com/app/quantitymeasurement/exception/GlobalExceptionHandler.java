package com.app.quantitymeasurement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * GlobalExceptionHandler - Centralized exception handling for all REST controllers.
 * Uses @ControllerAdvice to intercept exceptions and return consistent error responses.
 *
 * Three handlers:
 * 1. handleMethodArgumentNotValidException  - validation constraint failures (400)
 * 2. handleQuantityException               - QuantityMeasurementException (400)
 * 3. handleGlobalException                 - all other exceptions (500)
 *
 * @author Developer
 * @version 17.0
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles validation errors that occur when the input data fails to meet
     * the specified constraints. Extracts all field error messages and returns
     * a structured 400 Bad Request response.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex, WebRequest request) {

        String messages = ex.getBindingResult().getFieldErrors().stream()
            .map(fe -> fe.getDefaultMessage())
            .collect(Collectors.joining("; "));

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status",    HttpStatus.BAD_REQUEST.value());
        body.put("error",     "Quantity Measurement Error");
        body.put("message",   messages);
        body.put("path",      request.getDescription(false).replace("uri=", ""));

        return ResponseEntity.badRequest().body(body);
    }

    /**
     * Handles QuantityMeasurementException and returns a structured 400 Bad Request.
     */
    @ExceptionHandler(QuantityMeasurementException.class)
    public ResponseEntity<Map<String, Object>> handleQuantityException(
            QuantityMeasurementException ex, WebRequest request) {

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status",    HttpStatus.BAD_REQUEST.value());
        body.put("error",     "Quantity Measurement Error");
        body.put("message",   ex.getMessage());
        body.put("path",      request.getDescription(false).replace("uri=", ""));

        return ResponseEntity.badRequest().body(body);
    }

    /**
     * Catch-all handler for any other exceptions — returns 500 Internal Server Error.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(
            Exception ex, WebRequest request) {

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status",    HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error",     "Internal Server Error");
        body.put("message",   ex.getMessage());
        body.put("path",      request.getDescription(false).replace("uri=", ""));

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
