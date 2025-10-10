package com.yappyd.taskservice.controller;

import com.yappyd.taskservice.dto.TaskCreateRequest;
import com.yappyd.taskservice.dto.TaskResponse;
import com.yappyd.taskservice.dto.TaskUpdateRequest;
import com.yappyd.taskservice.service.TaskService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/task")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/create")
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskCreateRequest taskCreateRequest, @AuthenticationPrincipal Jwt jwt) {
        String username = jwt.getSubject();
        log.debug("Task creating request for user: {}", username);

        TaskResponse taskResponse = taskService.createTask(taskCreateRequest, username);
        log.debug("Created taskId {} response for user: {}", taskResponse.id(), username);

        return ResponseEntity.status(HttpStatus.CREATED).body(taskResponse);
    }

    @GetMapping("/list")
    public ResponseEntity<Page<TaskResponse>> getTasks(@AuthenticationPrincipal @PageableDefault(size = 10, sort = "deadline") Jwt jwt, Pageable pageable) {
        String username = jwt.getSubject();
        log.debug("Fetching tasks request for user: {}", username);

        Page<TaskResponse> tasks = taskService.getTasks(username, pageable);
        log.debug("Fetched {} tasks response for user: {}", tasks.getSize(), username);

        return ResponseEntity.ok(tasks);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable("id") Long id, @Valid @RequestBody TaskUpdateRequest updateRequest, @AuthenticationPrincipal Jwt jwt) {
        String username = jwt.getSubject();
        log.debug("Updating taskId {} request for user: {}", id, username);

        TaskResponse updatedTask = taskService.updateTask(id, updateRequest, username);
        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable("id") Long id, @AuthenticationPrincipal Jwt jwt) {
        String username = jwt.getSubject();
        log.debug("Deleting taskId {} request for user: {}", id, username);

        taskService.deleteTask(id, username);
        log.debug("Deleted taskId {} response for user: {}", id, username);

        return ResponseEntity.noContent().build();
    }

}
