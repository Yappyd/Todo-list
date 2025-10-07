package com.yappyd.authservice.exception;

import com.yappyd.authservice.service.JwtService;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String message, JwtService.TokenRole type, Throwable cause) {
        super("Invalid " + type.name().toLowerCase() + " token: " + message, cause);
    }

    public InvalidTokenException(String message, JwtService.TokenRole type) {
        super("Invalid " + type.name().toLowerCase() + " token: " + message);
    }
    public InvalidTokenException(String message, Throwable cause) {
        super("Invalid token: " + message, cause);
    }
}
