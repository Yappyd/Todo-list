package com.yappyd.taskservice.exception;

import com.yappyd.taskservice.dto.ErrorResponse;
import com.yappyd.taskservice.dto.TaskResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTaskNotFoundException(TaskNotFoundException ex) {
        log.warn("Task not found: {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse(
                "Task not found",
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value()
        );

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UsernameAccessException.class)
    public  ResponseEntity<ErrorResponse> handleUsernameAccessException(UsernameAccessException ex) {
        log.warn("Access denied: {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse(
                "Access denied",
                ex.getMessage(),
                HttpStatus.FORBIDDEN.value()
        );

        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        StringBuilder details = new StringBuilder();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            details.append(error.getField())
                    .append(": ")
                    .append(error.getDefaultMessage())
                    .append("; ");
        }
        log.warn("Validation failed: {}", details);

        ErrorResponse errorResponse = new ErrorResponse(
                "Validation failed",
                details.toString().trim(),
                HttpStatus.BAD_REQUEST.value()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex) {
        log.error("Unexpected error occurred", ex);

        ErrorResponse error = new ErrorResponse(
                "Internal Server Error",
                "An unexpected error has occurred",
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        return  new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
