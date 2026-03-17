package com.tanmeyah.practice.DTO.Requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TaskRequestDTO {

    @NotBlank
    @Size(min = 2, max = 200)
    private String title;

    @Size(max = 2000)
    private String description;
    private boolean completed;

    private Long userId;
}

