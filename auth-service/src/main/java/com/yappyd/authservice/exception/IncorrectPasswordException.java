package com.yappyd.authservice.exception;

public class IncorrectPasswordException extends RuntimeException {
    public IncorrectPasswordException(String username) {
        super("password for username '" + username + "' is incorrect.");
    }
}
