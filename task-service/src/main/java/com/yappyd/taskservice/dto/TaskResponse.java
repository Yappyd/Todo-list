package com.yappyd.taskservice.dto;

import com.yappyd.taskservice.model.TaskPriority;
import com.yappyd.taskservice.model.TaskStatus;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;

@Getter @Builder
public class TaskResponse {
    private final Long id;
    private final String title;
    private final String description;
    private final TaskStatus status;
    private final TaskPriority priority;
    private final LocalDate dueDate;
    private final String ownerUsername;
    private final Instant createdAt;
    private final Instant updatedAt;
}
