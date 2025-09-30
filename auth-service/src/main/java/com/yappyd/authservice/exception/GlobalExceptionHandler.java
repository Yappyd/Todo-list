package com.yappyd.authservice.exception;

import com.yappyd.authservice.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernameIsNotFound(UsernameNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
                "Authentication error",
                "Incorrect user name or password",
                HttpStatus.UNAUTHORIZED.value()
        );
        return  new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(IncorrectPasswordException.class)
    public ResponseEntity<ErrorResponse> handleIncorrectPassword(IncorrectPasswordException ex) {
        ErrorResponse error = new ErrorResponse(
                "Authentication error",
                "Incorrect user name or password",
                HttpStatus.UNAUTHORIZED.value()
        );
        return  new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleIncorrectPassword(UsernameAlreadyExistsException ex) {
        ErrorResponse error = new ErrorResponse(
                "Registration error",
                ex.getMessage(),
                HttpStatus.CONFLICT.value()
        );
        return  new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidToken(InvalidTokenException ex) {
        ErrorResponse error = new ErrorResponse(
                "Invalid token",
                ex.getMessage(),
                HttpStatus.UNAUTHORIZED.value()
        );
        return  new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex) {
        ErrorResponse error = new ErrorResponse(
                "Internal Server Error",
                "An unexpected error has occurred",
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        return  new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }
}
