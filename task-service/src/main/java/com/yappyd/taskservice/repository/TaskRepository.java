package com.yappyd.taskservice.repository;

import com.yappyd.taskservice.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findByOwnerUsername(String ownerUsername, Pageable pageable);
    Optional<Task> findByIdAndOwnerUsername(Long id, String ownerUsername);
    long deleteByIdAndOwnerUsername(Long id, String ownerUsername);
    Page<Task> findByOwnerUsernameAndStatus(String ownerUsername, TaskStatus status, Pageable pageable);
    Page<Task> findByOwnerUsernameAndPriority(String ownerUsername, TaskPriority priority, Pageable pageable);
    Page<Task> findByOwnerUsernameAndDueDateBetween(String ownerUsername, LocalDate from, LocalDate to, Pageable pageable);
}
