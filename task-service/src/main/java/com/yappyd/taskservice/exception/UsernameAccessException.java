package com.yappyd.taskservice.exception;

public class UsernameAccessException extends RuntimeException {
    public UsernameAccessException(String username, String taskId) {
        super("User '" + username + "' does not have access to task with id '" + taskId + "'.");
    }
}
