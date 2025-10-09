package com.yappyd.taskservice.dto;

import com.yappyd.taskservice.model.Task.*;

import java.time.LocalDate;

public record TaskResponse(
        Long id,
        String title,
        String description,
        TaskStatus status,
        TaskPriority priority,
        LocalDate deadline
) {
}
