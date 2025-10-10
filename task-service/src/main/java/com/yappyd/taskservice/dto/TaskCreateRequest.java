package com.yappyd.taskservice.dto;

import com.yappyd.taskservice.model.Task.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record TaskCreateRequest(
        @NotBlank (message = "Title must not be blank")
        @Size(max = 200, message = "Title must be at most 200 characters")
        String title,
        @Size(max = 10000, message = "Description must be at most 10000 characters")
        String description,
        @NotNull
        TaskPriority priority,
        @NotNull
        TaskStatus status,
        @FutureOrPresent (message = "Deadline must be today or in the future")
        LocalDate deadline
) {
}
