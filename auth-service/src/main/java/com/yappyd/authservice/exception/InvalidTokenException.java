package com.yappyd.authservice.exception;

import com.yappyd.authservice.service.TokenRole;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String message, TokenRole type, Throwable cause) {
        super("Invalid " + type.name().toLowerCase() + " token: " + message, cause);
    }
}
