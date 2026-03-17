package com.tanmeyah.practice.controller;

import com.tanmeyah.practice.DTO.Requests.AuthRequest;
import com.tanmeyah.practice.DTO.Requests.RegisterRequest;
import com.tanmeyah.practice.DTO.Requests.TaskRequestDTO;
import com.tanmeyah.practice.DTO.Requests.UserRequestDTO;
import com.tanmeyah.practice.DTO.Responses.AuthResponse;
import com.tanmeyah.practice.DTO.Responses.TaskResponseDTO;
import com.tanmeyah.practice.DTO.Responses.UserResponseDTO;
import com.tanmeyah.practice.service.AppService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AppController {

    private final AppService appService;

    // -------- AUTH --------
    @PostMapping("/auth/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return appService.register(request);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return appService.login(request);
    }

    // -------- USERS --------
    @PostMapping("/users")
    public UserResponseDTO createUser(@Valid @RequestBody UserRequestDTO request) {
        return appService.createUser(request);
    }

    @GetMapping("/users")
    public List<UserResponseDTO> getAllUsers() {
        return appService.getAllUsers();
    }

    @GetMapping("/users/{id}")
    public UserResponseDTO getUserById(@PathVariable Long id) {
        return appService.getUserById(id);
    }

    // -------- TASKS --------
    @PostMapping("/tasks")
    public TaskResponseDTO createTask(@Valid @RequestBody TaskRequestDTO request) {
        return appService.addTask(request);
    }

    @GetMapping("/tasks")
    public List<TaskResponseDTO> getAllTasks() {
        return appService.getAllTasks();
    }

    @GetMapping("/tasks/{id}")
    public TaskResponseDTO getTaskById(@PathVariable Long id) {
        return appService.getTaskById(id);
    }

    @PutMapping("/tasks/{id}")
    public TaskResponseDTO updateTask(@PathVariable Long id, @Valid @RequestBody TaskRequestDTO request) {
        return appService.updateTask(id, request);
    }

    @DeleteMapping("/tasks/{id}")
    public String deleteTask(@PathVariable Long id) {
        return appService.deleteTask(id) ? "Task deleted successfully" : "Task not found";
    }
}

