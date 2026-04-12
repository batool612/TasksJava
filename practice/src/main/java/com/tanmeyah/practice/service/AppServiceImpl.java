package com.tanmeyah.practice.service;

import com.tanmeyah.practice.DTO.Requests.AuthRequest;
import com.tanmeyah.practice.DTO.Requests.RegisterRequest;
import com.tanmeyah.practice.DTO.Requests.RegistrationRole;
import com.tanmeyah.practice.DTO.Requests.TaskRequestDTO;
import com.tanmeyah.practice.DTO.Requests.UserRequestDTO;
import com.tanmeyah.practice.DTO.Responses.AuthResponse;
import com.tanmeyah.practice.DTO.Responses.TaskResponseDTO;
import com.tanmeyah.practice.DTO.Responses.UserResponseDTO;
import com.tanmeyah.practice.Entity.Role;
import com.tanmeyah.practice.Entity.Task;
import com.tanmeyah.practice.Entity.User;
import com.tanmeyah.practice.Exception.ConflictException;
import com.tanmeyah.practice.Exception.ForbiddenException;
import com.tanmeyah.practice.Exception.NotFoundException;
import com.tanmeyah.practice.Repository.TaskRepository;
import com.tanmeyah.practice.Repository.UserRepository;
import com.tanmeyah.practice.Secuirty.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${app.admin-register-secret:}")
    private String adminRegisterSecret;

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final MessageSource messageSource;

    @Override
    public AuthResponse register(RegisterRequest request, String adminRegisterSecretHeader) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ConflictException(messageSource.getMessage(
                    "email.already.exists",
                    null,
                    LocaleContextHolder.getLocale()
            ));
        }

        // If user sends role → use it ,,,, If not → default = USER
        RegistrationRole registrationRole = request.getRole() != null ? request.getRole() : RegistrationRole.USER;
        // Request --> ADMIN ,,,, System ---> ROLE_ADMIN
        Role assignedRole = registrationRole == RegistrationRole.ADMIN ? Role.ROLE_ADMIN : Role.ROLE_USER;
        // If admin Must provide correct header secret
        if (assignedRole == Role.ROLE_ADMIN) {
            assertAdminRegistrationAllowed(adminRegisterSecretHeader);
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(assignedRole);

        User savedUser = userRepository.save(user);
        String token = jwtService.generateToken(savedUser);
        return toAuthResponse(token, savedUser.getRole());
    }

    @Override
    public AuthResponse login(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token;
        Role role;
        if (userDetails instanceof User user) {
            token = jwtService.generateToken(user);
            role = user.getRole();
        } else {
            token = jwtService.generateToken(userDetails);
            role = Role.ROLE_USER;
        }
        return toAuthResponse(token, role);
    }

    @Override
    public UserResponseDTO createUser(UserRequestDTO request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ConflictException(messageSource.getMessage(
                    "email.already.exists",
                    null,
                    LocaleContextHolder.getLocale()
            ));
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.ROLE_USER);

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
                .orElseThrow(() -> new NotFoundException(
                        messageSource.getMessage("user.not.found", null, LocaleContextHolder.getLocale())
                ));
    }

    @Override
    public TaskResponseDTO addTask(TaskRequestDTO request, User currentUser) {
        User user = getAuthenticatedUser(currentUser.getId());

        Task task = new Task();
        // Store multilingual content in DB
        task.setTitleEn(request.getTitleEn());
        task.setTitleAr(request.getTitleAr());
        task.setDescriptionEn(request.getDescriptionEn());
        task.setDescriptionAr(request.getDescriptionAr());
        task.setCompleted(request.getCompleted());
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
                .orElseThrow(() -> new NotFoundException(
                        messageSource.getMessage("task.not.found", null, LocaleContextHolder.getLocale())
                ));
    }

    @Override
    public TaskResponseDTO updateTask(Long id, TaskRequestDTO request, User currentUser) {
        User actor = getAuthenticatedUser(currentUser.getId());
        Task task = resolveTaskForWrite(id, actor);
        task.setTitleEn(request.getTitleEn());
        task.setTitleAr(request.getTitleAr());
        task.setDescriptionEn(request.getDescriptionEn());
        task.setDescriptionAr(request.getDescriptionAr());
        task.setCompleted(request.getCompleted());
        if (actor.getRole() != Role.ROLE_ADMIN) {
            task.setUser(actor);
        }
        Task updated = taskRepository.save(task);
        return mapToTaskResponse(updated);
    }

    @Override
    public boolean deleteTask(Long id, User currentUser) {
        User actor = getAuthenticatedUser(currentUser.getId());
        Task task = resolveTaskForWrite(id, actor);
        taskRepository.delete(task);
        return true;
    }

    //Single place for “can this actor touch this task?” on update/delete.


    private Task resolveTaskForWrite(Long id, User actor) {
        // Admin can update / delete any task
        if (actor.getRole() == Role.ROLE_ADMIN) {
            return taskRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException(
                            messageSource.getMessage("task.not.found", null, LocaleContextHolder.getLocale())
                    ));
        }
        return taskRepository.findByIdAndUserId(id, actor.getId())
                .orElseThrow(() -> new NotFoundException(
                        messageSource.getMessage("task.not.found", null, LocaleContextHolder.getLocale())
                ));
    }

    private UserResponseDTO mapToUserResponse(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name()
        );
    }

    private TaskResponseDTO mapToTaskResponse(Task task) {

        String lang = LocaleContextHolder.getLocale().getLanguage();

        String titleEn = null;
        String titleAr = null;
        String descriptionEn = null;
        String descriptionAr = null;

        if ("ar".equalsIgnoreCase(lang)) {
            titleAr = task.getTitleAr();
            descriptionAr = task.getDescriptionAr();
        } else {
            titleEn = task.getTitleEn();
            descriptionEn = task.getDescriptionEn();
        }

        return new TaskResponseDTO(
                task.getId(),
                titleEn,
                titleAr,
                descriptionEn,
                descriptionAr,
                task.getCompleted(),
                task.getUser() != null ? task.getUser().getId() : null,
                task.getUser() != null ? task.getUser().getName() : null
        );
    }

    private User getAuthenticatedUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(
                        messageSource.getMessage("user.not.found", null, LocaleContextHolder.getLocale())
                ));
    }

    //Single builder for register and login so role and accountType stay in sync.
    private AuthResponse toAuthResponse(String token, Role role) {
        return new AuthResponse(token, role.name(), role == Role.ROLE_ADMIN ? "admin" : "user");
    }


    // This is the gate that stops random clients from registering as ADMIN without knowing the server secret.
    private void assertAdminRegistrationAllowed(String header) {
        //If no secret configured then Admin registration is disabled
        if (adminRegisterSecret == null || adminRegisterSecret.isBlank()) {
            throw new ForbiddenException(messageSource.getMessage(
                    "admin.register.disabled",
                    null,
                    LocaleContextHolder.getLocale()
            ));
        }
        String provided = header != null ? header.trim() : "";
        if (!adminRegisterSecret.equals(provided)) {
            throw new ForbiddenException(messageSource.getMessage(
                    "admin.register.secret.invalid",
                    null,
                    LocaleContextHolder.getLocale()
            ));
        }
    }
}