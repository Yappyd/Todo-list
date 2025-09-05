package com.yappyd.taskservice.web.controller;

import com.yappyd.taskservice.dto.*;
import com.yappyd.taskservice.model.TaskPriority;
import com.yappyd.taskservice.model.TaskStatus;
import com.yappyd.taskservice.service.TaskService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Tasks")
@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponse> create(@Valid @RequestBody TaskCreateRequest req,
                                               Authentication auth) {
        TaskResponse created = taskService.create(req, auth.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public TaskResponse getOne(@PathVariable Long id, Authentication auth) {
        return taskService.getById(id, auth);
    }

    @GetMapping
    public Page<TaskResponse> list(@RequestParam(defaultValue = "false") boolean all,
                                   Pageable pageable,
                                   Authentication auth) {

        if (all) {
            return taskService.listAllForAdmin(pageable);
        }
        return taskService.listMine(auth.getName(), pageable);
    }

    @PatchMapping("/{id}")
    public TaskResponse update(@PathVariable Long id,
                               @RequestBody TaskUpdateRequest req,
                               Authentication auth) {
        return taskService.updatePartial(id, req, auth);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
        taskService.delete(id, auth);
        return ResponseEntity.noContent().build();
    }
}
