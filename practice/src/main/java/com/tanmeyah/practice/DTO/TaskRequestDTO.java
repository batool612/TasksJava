package com.tanmeyah.practice.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TaskRequestDTO {

    @NotBlank
    private String title;

    private String description;
    private boolean completed;

    private Long userId;
}