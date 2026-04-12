package com.tanmeyah.practice.service;

import com.tanmeyah.practice.DTO.Requests.AuthRequest;
import com.tanmeyah.practice.DTO.Requests.RegisterRequest;
import com.tanmeyah.practice.DTO.Requests.TaskRequestDTO;
import com.tanmeyah.practice.DTO.Requests.UserRequestDTO;
import com.tanmeyah.practice.DTO.Responses.AuthResponse;
import com.tanmeyah.practice.DTO.Responses.TaskResponseDTO;
import com.tanmeyah.practice.DTO.Responses.UserResponseDTO;
import com.tanmeyah.practice.Entity.User;

import java.util.List;

public interface AppService {
    AuthResponse register(RegisterRequest request, String adminRegisterSecretHeader);

    AuthResponse login(AuthRequest request);

    UserResponseDTO createUser(UserRequestDTO request);

    List<UserResponseDTO> getAllUsers();

    UserResponseDTO getUserById(Long id);

    TaskResponseDTO addTask(TaskRequestDTO request, User currentUser);

    List<TaskResponseDTO> getAllTasks();

    TaskResponseDTO getTaskById(Long id);

    TaskResponseDTO updateTask(Long id, TaskRequestDTO request, User currentUser);

    boolean deleteTask(Long id, User currentUser);
}