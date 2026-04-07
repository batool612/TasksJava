package com.tanmeyah.practice.controller;

import com.tanmeyah.practice.DTO.Requests.AuthRequest;
import com.tanmeyah.practice.DTO.Requests.RegisterRequest;
import com.tanmeyah.practice.DTO.Requests.TaskRequestDTO;
import com.tanmeyah.practice.DTO.Requests.UserRequestDTO;
import com.tanmeyah.practice.DTO.Responses.AuthResponse;
import com.tanmeyah.practice.DTO.Responses.TaskResponseDTO;
import com.tanmeyah.practice.DTO.Responses.UserResponseDTO;
import com.tanmeyah.practice.Entity.User;
import com.tanmeyah.practice.Exception.UnauthorizedException;
import com.tanmeyah.practice.service.AppService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
    public TaskResponseDTO createTask(@Valid @RequestBody TaskRequestDTO request, Authentication authentication) {
        Long userId = extractUserId(authentication);
        return appService.addTask(request, userId);
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
    public TaskResponseDTO updateTask(@PathVariable Long id, @Valid @RequestBody TaskRequestDTO request, Authentication authentication) {
        Long userId = extractUserId(authentication);
        return appService.updateTask(id, request, userId);
    }

    @DeleteMapping("/tasks/{id}")
    public String deleteTask(@PathVariable Long id, Authentication authentication) {
        Long userId = extractUserId(authentication);
        return appService.deleteTask(id, userId) ? "Task deleted successfully" : "Task not found";
    }

    //Get the logged-in user’s ID from Spring Security’s Authentication object
    private Long extractUserId(Authentication authentication) {
        //Is the user logged in? ,,, Is there a principal object?
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new UnauthorizedException("Missing authentication principal");
        }
        //Check if principal is of type User ,, If yes → cast it automatically to user
        //principal = the currently authenticated user
        if (authentication.getPrincipal() instanceof User user) {
            return user.getId();
        }
        throw new UnauthorizedException("Unexpected authentication principal type: " + authentication.getPrincipal().getClass().getName());
    }
}