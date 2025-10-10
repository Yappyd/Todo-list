package com.yappyd.taskservice.service;

import com.yappyd.taskservice.dto.TaskCreateRequest;
import com.yappyd.taskservice.dto.TaskResponse;
import com.yappyd.taskservice.dto.TaskUpdateRequest;
import com.yappyd.taskservice.exception.TaskNotFoundException;
import com.yappyd.taskservice.exception.UsernameAccessException;
import com.yappyd.taskservice.model.Task;
import com.yappyd.taskservice.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task task;
    private TaskCreateRequest createRequest;
    private TaskUpdateRequest updateRequest;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        task = Task.builder()
                .id(1L)
                .username("testuser")
                .title("Test Task")
                .description("Description")
                .status(Task.TaskStatus.TODO)
                .priority(Task.TaskPriority.MEDIUM)
                .deadline(LocalDate.of(2025, 10, 10))
                .build();

        createRequest = new TaskCreateRequest(
                "Test Task",
                "Description",
                Task.TaskPriority.MEDIUM,
                Task.TaskStatus.TODO,
                LocalDate.of(2025, 10, 10)
        );

        updateRequest = new TaskUpdateRequest(
                "Updated Task",
                null,
                Task.TaskPriority.HIGH,
                null,
                null
        );

        pageable = PageRequest.of(0, 10);
    }

    @Test
    void shouldCreateTaskSuccessfully() {
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskResponse response = taskService.createTask(createRequest, "testuser");

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Test Task", response.title());
        assertEquals("Description", response.description());
        assertEquals(Task.TaskStatus.TODO, response.status());
        assertEquals(Task.TaskPriority.MEDIUM, response.priority());
        assertEquals(LocalDate.of(2025, 10, 10), response.deadline());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void shouldGetTasksSuccessfully() {
        Page<Task> tasks = new PageImpl<>(List.of(task));
        when(taskRepository.findByUsername("testuser", pageable)).thenReturn(tasks);

        Page<TaskResponse> response = taskService.getTasks("testuser", pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals("Test Task", response.getContent().get(0).title());
        assertEquals(Task.TaskPriority.MEDIUM, response.getContent().get(0).priority());
        verify(taskRepository, times(1)).findByUsername("testuser", pageable);
    }
    @Test
    void shouldReturnEmptyPageWhenNoTasksExist() {
        Page<Task> emptyPage = new PageImpl<>(List.of());
        when(taskRepository.findByUsername("testuser", pageable)).thenReturn(emptyPage);

        Page<TaskResponse> response = taskService.getTasks("testuser", pageable);

        assertNotNull(response);
        assertEquals(0, response.getTotalElements());
        verify(taskRepository, times(1)).findByUsername("testuser", pageable);
    }

    @Test
    void shouldUpdateTaskSuccessfully() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskResponse response = taskService.updateTask(1L, updateRequest, "testuser");

        assertNotNull(response);
        assertEquals("Updated Task", response.title());
        assertEquals(Task.TaskPriority.HIGH, response.priority());
        assertEquals("Description", response.description());
        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).save(any(Task.class));
    }
    @Test
    void shouldThrowTaskNotFoundExceptionForNonExistentTaskInUpdate() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.updateTask(1L, updateRequest, "testuser"));
        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, never()).save(any(Task.class));
    }
    @Test
    void shouldThrowUsernameAccessExceptionForUnauthorizedUserInUpdate() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        assertThrows(UsernameAccessException.class, () -> taskService.updateTask(1L, updateRequest, "otheruser"));
        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void shouldDeleteTaskSuccessfully() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        taskService.deleteTask(1L, "testuser");

        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).delete(task);
    }
    @Test
    void shouldThrowTaskNotFoundExceptionForNonExistentTaskInDelete() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.deleteTask(1L, "testuser"));
        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, never()).delete(any(Task.class));
    }
    @Test
    void shouldThrowUsernameAccessExceptionForUnauthorizedUserInDelete() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        assertThrows(UsernameAccessException.class, () -> taskService.deleteTask(1L, "otheruser"));
        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, never()).delete(any(Task.class));
    }
}