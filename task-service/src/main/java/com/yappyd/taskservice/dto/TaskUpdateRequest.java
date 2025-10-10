package com.yappyd.taskservice.dto;

import com.yappyd.taskservice.model.Task.TaskPriority;
import com.yappyd.taskservice.model.Task.TaskStatus;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record TaskUpdateRequest(
        @Size(max = 200, message = "Title must be at most 200 characters")
        String title,
        @Size(max = 10000, message = "Description must be at most 10000 characters")
        String description,
        TaskPriority priority,
        TaskStatus status,
        LocalDate deadline
) {}
