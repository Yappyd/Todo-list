package com.yappyd.taskservice.dto;

import com.yappyd.taskservice.model.Task.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record TaskCreateRequest(
        @NotBlank (message = "Title must not be blank")
        @Size(max = 200)
        String title,
        @Size(max = 10000)
        String description,
        @NotNull (message = "Priority must not be null")
        TaskPriority priority,
        @NotNull (message = "Status must not be null")
        TaskStatus status,
        @FutureOrPresent (message = "Deadline must be today or in the future")
        LocalDate deadline
) {
}
