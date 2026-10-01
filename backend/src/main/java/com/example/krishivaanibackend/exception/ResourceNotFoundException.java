package com.example.krishivaanibackend.exception;

/**
 * Thrown when a requested resource (question, document, etc.) is not found in the database.
 * Maps to Http 404 via the GlobalExceptionHandler.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
    public ResourceNotFoundException(String resourceName, Long id) {
        super(resourceName + " with id " + id + " not found");
    }

}
