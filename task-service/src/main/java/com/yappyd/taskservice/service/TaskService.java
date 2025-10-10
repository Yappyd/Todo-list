package com.yappyd.taskservice.service;

import com.yappyd.taskservice.dto.TaskCreateRequest;
import com.yappyd.taskservice.dto.TaskResponse;
import com.yappyd.taskservice.dto.TaskUpdateRequest;
import com.yappyd.taskservice.exception.TaskNotFoundException;
import com.yappyd.taskservice.exception.UsernameAccessException;
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
        task = taskRepository.save(task);
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
        log.info("Updating Task {} for user: {}", taskId, username);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId.toString()));
        if (!task.getUsername().equals(username)) {
            throw new UsernameAccessException(username, taskId.toString());
        }
        if (updateRequest.title() != null) task.setTitle(updateRequest.title());
        if (updateRequest.description() != null) task.setDescription(updateRequest.description());
        if (updateRequest.priority() != null) task.setPriority(updateRequest.priority());
        if (updateRequest.status() != null) task.setStatus(updateRequest.status());
        if (updateRequest.deadline() != null) task.setDeadline(updateRequest.deadline());

        task = taskRepository.save(task);
        log.info("Updated Task {} for user: {}", taskId, username);

        return new TaskResponse(task.getId(), task.getTitle(), task.getDescription(), task.getStatus(), task.getPriority(), task.getDeadline());
    }

    public void deleteTask(Long taskId, String username) {
        log.info("Deleting Task {} for user: {}", taskId, username);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId.toString()));
        if (!task.getUsername().equals(username)) {
            throw new UsernameAccessException(username, taskId.toString());
        }
        taskRepository.delete(task);
        log.info("Deleted Task {} for user: {}", taskId, username);
    }
}
