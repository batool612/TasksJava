package com.tanmeyah.practice.Repository;

import com.tanmeyah.practice.Entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // JpaRepository already provides:
    // - List<Task> findAll()
    // - Optional<Task> findById(Long id)
    // - Task save(Task task)
    // - void deleteById(Long id)
    // - boolean existsById(Long id)
}