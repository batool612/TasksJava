package com.tanmeyah.practice.service;

import com.tanmeyah.practice.DTO.TaskRequestDTO;
import com.tanmeyah.practice.DTO.TaskResponseDTO;
import com.tanmeyah.practice.Entity.Task;
import com.tanmeyah.practice.Repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    // CREATE
    public TaskResponseDTO addTask(TaskRequestDTO request) {
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setCompleted(request.isCompleted());

        Task saved = taskRepository.save(task);
        return mapToResponse(saved);
    }

    // GET ALL
    public List<TaskResponseDTO> getAllTasks() {
        return taskRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList()); // collect back into list
    }

    // GET BY ID
    public TaskResponseDTO getTaskById(Long id) {
        return taskRepository.findById(id)
                .map(this::mapToResponse)
                .orElse(null);
    }

    // UPDATE
    public TaskResponseDTO updateTask(Long id, TaskRequestDTO request) {
        return taskRepository.findById(id)
                .map(task -> {
                    task.setTitle(request.getTitle());
                    task.setDescription(request.getDescription());
                    task.setCompleted(request.isCompleted());
                    Task updated = taskRepository.save(task);
                    return mapToResponse(updated);
                })
                .orElse(null);
    }

    // DELETE
    public boolean deleteTask(Long id) {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private TaskResponseDTO mapToResponse(Task task) {
        // user-related fields are null for now as Task is not linked to User
        return new TaskResponseDTO(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.isCompleted(),
                null, // lesa , mafesh relation ll user
                null
        );
    }
}