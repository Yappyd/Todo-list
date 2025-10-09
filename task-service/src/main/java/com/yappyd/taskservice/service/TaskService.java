package com.yappyd.taskservice.service;

import com.yappyd.taskservice.dto.TaskCreateRequest;
import com.yappyd.taskservice.dto.TaskResponse;
import com.yappyd.taskservice.dto.TaskUpdateRequest;
import com.yappyd.taskservice.model.Task;
import com.yappyd.taskservice.repository.TaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Slf4j
@Service
public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public TaskResponse createTask(TaskCreateRequest taskCreateRequest, String username) {
        log.info("Creating task for user: {}", username);

        Task task = Task.builder()
                .title(taskCreateRequest.title())
                .description(taskCreateRequest.description())
                .priority(taskCreateRequest.priority())
                .status(taskCreateRequest.status())
                .deadline(taskCreateRequest.deadline())
                .username(username)
                .build();
        taskRepository.save(task);
        log.info("Task {} created for user: {}", task.getId(), username);

        return new TaskResponse(task.getId(), task.getTitle(), task.getDescription(), task.getStatus(), task.getPriority(), task.getDeadline());
    }

    public Page<TaskResponse> getTasks(String username, Pageable pageable) {
        log.info("Fetching tasks for user: {}", username);

        Page<Task> tasks = taskRepository.findByUsername(username, pageable);
        log.info("Fetched {} tasks for user: {}", tasks.getTotalElements(), username);

        return tasks.map(task -> new TaskResponse(task.getId(), task.getTitle(), task.getDescription(), task.getStatus(), task.getPriority(), task.getDeadline()));
    }

    public TaskResponse updateTask(Long taskId, TaskUpdateRequest updateRequest, String username) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!task.getUsername().equals(username)) {
            throw new RuntimeException("You are not allowed to update this task");
        }

        if (updateRequest.title() != null) task.setTitle(updateRequest.title());
        if (updateRequest.description() != null) task.setDescription(updateRequest.description());
        if (updateRequest.priority() != null) task.setPriority(updateRequest.priority());
        if (updateRequest.status() != null) task.setStatus(updateRequest.status());
        if (updateRequest.deadline() != null) task.setDeadline(updateRequest.deadline());

        taskRepository.save(task);

        return new TaskResponse(task.getId(), task.getTitle(), task.getDescription(), task.getStatus(), task.getPriority(), task.getDeadline());
    }

    public void deleteTask(Long taskId, String username) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!task.getUsername().equals(username)) {
            throw new RuntimeException("You are not allowed to delete this task");
        }

        taskRepository.delete(task);
    }


}
