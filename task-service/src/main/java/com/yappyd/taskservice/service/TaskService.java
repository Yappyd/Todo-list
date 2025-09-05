package com.yappyd.taskservice.service;

import com.yappyd.taskservice.dto.*;
import com.yappyd.taskservice.model.*;
import com.yappyd.taskservice.repository.TaskRepository;
import com.yappyd.taskservice.web.error.ForbiddenException;
import com.yappyd.taskservice.web.error.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    private boolean isAdmin(Authentication auth) {
        return auth != null && auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> Objects.equals(a, "ROLE_ADMIN"));
    }

    public TaskResponse create(TaskCreateRequest req, String ownerUsername) {
        Task task = Task.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .priority(req.getPriority() != null ? req.getPriority() : TaskPriority.MEDIUM)
                .status(TaskStatus.TODO)
                .dueDate(req.getDueDate())
                .ownerUsername(ownerUsername)
                .build();
        Task saved = taskRepository.save(task);
        return toResponse(saved);
    }

    public TaskResponse getById(Long id, Authentication auth) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Task not found: " + id));
        if (!isAdmin(auth) && !task.getOwnerUsername().equals(auth.getName())) {
            throw new ForbiddenException("Access denied");
        }
        return toResponse(task);
    }

    public Page<TaskResponse> listMine(String ownerUsername, Pageable pageable) {
        return taskRepository.findByOwnerUsername(ownerUsername, pageable).map(this::toResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Page<TaskResponse> listAllForAdmin(Pageable pageable) {
        return taskRepository.findAll(pageable).map(this::toResponse);
    }

    public TaskResponse updatePartial(Long id, TaskUpdateRequest req, Authentication auth) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Task not found: " + id));

        if (!isAdmin(auth) && !task.getOwnerUsername().equals(auth.getName())) {
            throw new ForbiddenException("Access denied");
        }

        if (req.getTitle() != null) task.setTitle(req.getTitle());
        if (req.getDescription() != null) task.setDescription(req.getDescription());
        if (req.getStatus() != null) task.setStatus(req.getStatus());
        if (req.getPriority() != null) task.setPriority(req.getPriority());
        if (req.getDueDate() != null) task.setDueDate(req.getDueDate());

        Task saved = taskRepository.save(task);
        return toResponse(saved);
    }

    public void delete(Long id, Authentication auth) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Task not found: " + id));
        if (!isAdmin(auth) && !task.getOwnerUsername().equals(auth.getName())) {
            throw new ForbiddenException("Access denied");
        }
        taskRepository.deleteById(id);
    }

    private TaskResponse toResponse(Task t) {
        return TaskResponse.builder()
                .id(t.getId())
                .title(t.getTitle())
                .description(t.getDescription())
                .status(t.getStatus())
                .priority(t.getPriority())
                .dueDate(t.getDueDate())
                .ownerUsername(t.getOwnerUsername())
                .createdAt(t.getCreatedAt())
                .updatedAt(t.getUpdatedAt())
                .build();
    }
}
