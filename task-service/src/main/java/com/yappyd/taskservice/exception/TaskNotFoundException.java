package com.yappyd.taskservice.exception;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(String taskId) {
        super("Task with id '" + taskId + "' is not found.");
    }
}
