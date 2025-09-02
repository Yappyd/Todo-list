package com.yappyd.taskservice.dto;

import com.yappyd.taskservice.model.TaskPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter
public class TaskCreateRequest {
    @NotBlank
    @Size(max = 200)
    private String title;

    @Size(max = 10000)
    private String description;

    private TaskPriority priority = TaskPriority.MEDIUM;

    private LocalDate dueDate;
}
