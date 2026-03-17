package com.tanmeyah.practice.service;

import com.tanmeyah.practice.DTO.Requests.AuthRequest;
import com.tanmeyah.practice.DTO.Requests.RegisterRequest;
import com.tanmeyah.practice.DTO.Requests.TaskRequestDTO;
import com.tanmeyah.practice.DTO.Requests.UserRequestDTO;
import com.tanmeyah.practice.DTO.Responses.AuthResponse;
import com.tanmeyah.practice.DTO.Responses.TaskResponseDTO;
import com.tanmeyah.practice.DTO.Responses.UserResponseDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AppService {
    ResponseEntity<AuthResponse> register(RegisterRequest request);

    ResponseEntity<AuthResponse> login(AuthRequest request);

    UserResponseDTO createUser(UserRequestDTO request);

    List<UserResponseDTO> getAllUsers();

    UserResponseDTO getUserById(Long id);

    TaskResponseDTO addTask(TaskRequestDTO request);

    List<TaskResponseDTO> getAllTasks();

    TaskResponseDTO getTaskById(Long id);

    TaskResponseDTO updateTask(Long id, TaskRequestDTO request);

    boolean deleteTask(Long id);
}

