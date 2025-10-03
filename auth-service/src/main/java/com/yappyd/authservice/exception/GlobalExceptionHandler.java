package com.yappyd.authservice.exception;

import com.yappyd.authservice.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernameIsNotFound(UsernameNotFoundException ex) {
        log.warn("Authentication failed: user not found. Message={}", ex.getMessage());

        ErrorResponse error = new ErrorResponse(
                "Authentication error",
                "Incorrect user name or password",
                HttpStatus.UNAUTHORIZED.value()
        );
        return  new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(IncorrectPasswordException.class)
    public ResponseEntity<ErrorResponse> handleIncorrectPassword(IncorrectPasswordException ex) {
        log.warn("Authentication failed: incorrect password. Message={}", ex.getMessage());

        ErrorResponse error = new ErrorResponse(
                "Authentication error",
                "Incorrect user name or password",
                HttpStatus.UNAUTHORIZED.value()
        );
        return  new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleIncorrectPassword(UsernameAlreadyExistsException ex) {
        log.info("Registration failed: username already exists. Message={}", ex.getMessage());

        ErrorResponse error = new ErrorResponse(
                "Registration error",
                ex.getMessage(),
                HttpStatus.CONFLICT.value()
        );
        return  new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidToken(InvalidTokenException ex) {
        log.warn("Token validation failed: {}", ex.getMessage(), ex);

        ErrorResponse error = new ErrorResponse(
                "Invalid token",
                ex.getMessage(),
                HttpStatus.UNAUTHORIZED.value()
        );
        return  new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex) {
        log.error("Unexpected error occurred", ex);

        ErrorResponse error = new ErrorResponse(
                "Internal Server Error",
                "An unexpected error has occurred",
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        return  new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }
}
