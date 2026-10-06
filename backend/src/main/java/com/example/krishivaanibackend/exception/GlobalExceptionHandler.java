package com.example.krishivaanibackend.exception;
import com.example.krishivaanibackend.dto.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
/**
 * Centralised exception → JSON error response mapping.
 * Every controller in the app is covered automatically.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    // ── 400: Bean Validation failures (@Valid) ─────────────────────────────────
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        List<String> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());
        ErrorResponseDTO body = new ErrorResponseDTO(
                400, "Bad Request",
                "Validation failed. Check 'details' for field errors.",
                details, request.getRequestURI());
        return ResponseEntity.badRequest().body(body);
    }
    // ── 400: Wrong path variable type (e.g. /questions/abc instead of a number)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDTO> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {
        String detail = "Parameter '" + ex.getName() + "' should be of type "
                + ex.getRequiredType().getSimpleName();
        ErrorResponseDTO body = new ErrorResponseDTO(
                400, "Bad Request", "Invalid parameter type",
                List.of(detail), request.getRequestURI());
        return ResponseEntity.badRequest().body(body);
    }
    // ── 404: Resource not found ────────────────────────────────────────────────
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request) {
        ErrorResponseDTO body = new ErrorResponseDTO(
                404, "Not Found", ex.getMessage(),
                List.of(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }
    // ── 413: Audio file too large ──────────────────────────────────────────────
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponseDTO> handleFileTooLarge(
            MaxUploadSizeExceededException ex,
            HttpServletRequest request) {
        ErrorResponseDTO body = new ErrorResponseDTO(
                413, "Payload Too Large",
                "Uploaded file exceeds the maximum allowed size (25 MB).",
                List.of(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(body);
    }
    // ── 500: Catch-all ─────────────────────────────────────────────────────────
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGeneric(
            Exception ex,
            HttpServletRequest request) {
        ErrorResponseDTO body = new ErrorResponseDTO(
                500, "Internal Server Error",
                "An unexpected error occurred. Please try again later.",
                List.of(ex.getMessage()), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }


    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String,String>> handleRuntimeException(RuntimeException ex){
        return ResponseEntity.badRequest().body(Map.of("message",ex.getMessage()));
    }
}
