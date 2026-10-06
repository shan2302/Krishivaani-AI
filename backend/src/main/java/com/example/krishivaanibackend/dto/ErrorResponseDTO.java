package com.example.krishivaanibackend.dto;

import java.time.LocalDateTime;
import java.util.List;

//Standard error envelope returned by GlobalExceptionHandlet
public class ErrorResponseDTO {
    private int status;
    private String error;
    private String message;
    private List<String> details;
    private LocalDateTime timestamp;
    private String path;

    public ErrorResponseDTO(int status, String error, String message, List<String> details, String path) {

        this.status = status;
        this.error = error;
        this.message = message;
        this.details = details;
        this.timestamp = LocalDateTime.now();
        this.path = path;
    }

//  Getters
    public int getStatus() { return status; }
    public String getError() { return error; }
    public String getMessage() { return message; }
    public List<String> getDetails() { return details; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getPath() { return path; }
}
