package com.yappyd.authservice.exception;

public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException(String username) {
        super("username '" + username + "' already exists.");
    }
}
