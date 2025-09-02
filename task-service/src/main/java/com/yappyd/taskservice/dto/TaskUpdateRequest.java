package com.yappyd.taskservice.dto;

import com.yappyd.taskservice.model.TaskPriority;
import com.yappyd.taskservice.model.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter
public class TaskUpdateRequest {
    @NotBlank
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDate dueDate;
}
