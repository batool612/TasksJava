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
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request,
            @RequestHeader(value = "X-Admin-Register-Secret", required = false) String adminRegisterSecret
    ) {
        AuthResponse body = appService.register(request, adminRegisterSecret);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        AuthResponse body = appService.login(request);
        return ResponseEntity.ok(body);
    }

    // -------- USERS --------
    @PostMapping("/users")
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO request) {
        UserResponseDTO body = appService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(appService.getAllUsers());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(appService.getUserById(id));
    }

    // -------- TASKS --------
    @PostMapping("/tasks")
    public ResponseEntity<TaskResponseDTO> createTask(
            @Valid @RequestBody TaskRequestDTO request,
            Authentication authentication
    ) {
        TaskResponseDTO body = appService.addTask(request, extractUser(authentication));
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @GetMapping("/tasks")
    public ResponseEntity<List<TaskResponseDTO>> getAllTasks() {
        return ResponseEntity.ok(appService.getAllTasks());
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity<TaskResponseDTO> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(appService.getTaskById(id));
    }

    @PutMapping("/tasks/{id}")
    public ResponseEntity<TaskResponseDTO> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequestDTO request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(appService.updateTask(id, request, extractUser(authentication)));
    }
    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<String> deleteTask(@PathVariable Long id, Authentication authentication) {
        appService.deleteTask(id, extractUser(authentication));
        return ResponseEntity.ok("Task deleted successfully");
    }
    private User extractUser(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new UnauthorizedException("Missing authentication principal");
        }
        if (authentication.getPrincipal() instanceof User user) {
            return user;
        }
        throw new UnauthorizedException("Unexpected authentication principal type: " + authentication.getPrincipal().getClass().getName());
    }
}