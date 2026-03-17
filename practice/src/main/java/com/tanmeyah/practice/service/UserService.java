package com.tanmeyah.practice.service;

import com.tanmeyah.practice.DTO.UserRequestDTO;
import com.tanmeyah.practice.DTO.UserResponseDTO;
import com.tanmeyah.practice.Entity.User;
import com.tanmeyah.practice.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponseDTO createUser(UserRequestDTO request) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        // password is not part of this DTO; password is checked but not here

        User saved = userRepository.save(user);
        return mapToResponse(saved);
    }

    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public UserResponseDTO getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::mapToResponse)
                .orElse(null);
    }
///converts entity to DTos
    private UserResponseDTO mapToResponse(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }
}