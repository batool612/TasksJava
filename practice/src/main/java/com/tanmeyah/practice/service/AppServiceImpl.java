package com.tanmeyah.practice.service;

import com.tanmeyah.practice.DTO.Requests.AuthRequest;
import com.tanmeyah.practice.DTO.Requests.RegisterRequest;
import com.tanmeyah.practice.DTO.Requests.TaskRequestDTO;
import com.tanmeyah.practice.DTO.Requests.UserRequestDTO;
import com.tanmeyah.practice.DTO.Responses.AuthResponse;
import com.tanmeyah.practice.DTO.Responses.TaskResponseDTO;
import com.tanmeyah.practice.DTO.Responses.UserResponseDTO;
import com.tanmeyah.practice.Entity.Task;
import com.tanmeyah.practice.Entity.User;
import com.tanmeyah.practice.Exception.ConflictException;
import com.tanmeyah.practice.Exception.NotFoundException;
import com.tanmeyah.practice.Repository.TaskRepository;
import com.tanmeyah.practice.Repository.UserRepository;
import com.tanmeyah.practice.Secuirty.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppServiceImpl implements AppService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public ResponseEntity<AuthResponse> register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("ROLE_USER");

        User savedUser = userRepository.save(user);
        String token = jwtService.generateToken(savedUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponse(token));
    }

    @Override
    public ResponseEntity<AuthResponse> login(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token;
        if (userDetails instanceof User user) {
            token = jwtService.generateToken(user);
        } else {
            token = jwtService.generateToken(userDetails);
        }
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @Override
    public UserResponseDTO createUser(UserRequestDTO request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ConflictException("User with this email already exists");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());

        User saved = userRepository.save(user);
        return mapToUserResponse(saved);
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDTO getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::mapToUserResponse)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    public TaskResponseDTO addTask(TaskRequestDTO request, Long userId) {
        User user = getAuthenticatedUser(userId);

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setCompleted(request.isCompleted());
        task.setUser(user);

        Task saved = taskRepository.save(task);
        return mapToTaskResponse(saved);
    }

    @Override
    public List<TaskResponseDTO> getAllTasks() {
        return taskRepository.findAll()
                .stream()
                .map(this::mapToTaskResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TaskResponseDTO getTaskById(Long id) {
        return taskRepository.findById(id)
                .map(this::mapToTaskResponse)
                .orElseThrow(() -> new NotFoundException("Task not found"));
    }

    @Override
    public TaskResponseDTO updateTask(Long id, TaskRequestDTO request, Long userId) {
        User user = getAuthenticatedUser(userId);
        return taskRepository.findByIdAndUserId(id, user.getId())
                .map(task -> {
                    task.setTitle(request.getTitle());
                    task.setDescription(request.getDescription());
                    task.setCompleted(request.isCompleted());
                    task.setUser(user);
                    Task updated = taskRepository.save(task);
                    return mapToTaskResponse(updated);
                })
                .orElseThrow(() -> new NotFoundException("Task not found"));
    }

    @Override
    public boolean deleteTask(Long id, Long userId) {
        User user = getAuthenticatedUser(userId);
        Task task = taskRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new NotFoundException("Task not found"));
        taskRepository.delete(task);
        return true;
    }

    private UserResponseDTO mapToUserResponse(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    private TaskResponseDTO mapToTaskResponse(Task task) {
        return new TaskResponseDTO(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.isCompleted(),
                task.getUser() != null ? task.getUser().getId() : null,
                task.getUser() != null ? task.getUser().getName() : null
        );
    }

    private User getAuthenticatedUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Authenticated user not found"));
    }
}