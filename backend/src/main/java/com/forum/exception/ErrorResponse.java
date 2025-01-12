package com.forum.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

    public static ErrorResponse of(String message, int status, String error, String path) {
        return new ErrorResponse(LocalDateTime.now(), status, error, message, path);
    }
} 