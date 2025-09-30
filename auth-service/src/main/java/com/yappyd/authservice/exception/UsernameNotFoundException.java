package com.yappyd.authservice.exception;

public class UsernameNotFoundException extends RuntimeException {
    public UsernameNotFoundException(String username) {
        super("username '" + username + "' is not found.");
    }
}
