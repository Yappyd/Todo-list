package com.yappyd.taskservice.repository;

import com.yappyd.taskservice.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
    boolean existsById(Long id);
    Page<Task> findByUsername(String username, Pageable pageable);
    void deleteById(Long id);
}
