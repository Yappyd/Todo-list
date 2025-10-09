package com.yappyd.taskservice.dto;

import com.yappyd.taskservice.model.Task.TaskPriority;
import com.yappyd.taskservice.model.Task.TaskStatus;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record TaskUpdateRequest(
        @Size(max = 200)
        String title,
        @Size(max = 10000)
        String description,
        TaskPriority priority,
        TaskStatus status,
        LocalDate deadline
) {}
