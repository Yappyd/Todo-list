package com.yappyd.taskservice.dto;

public record ErrorResponse(
        String error,
        String message,
        int status,
        long timestamp
) {
    public ErrorResponse(String error, String message, int status) {
        this(error, message, status, System.currentTimeMillis());
    }
}
