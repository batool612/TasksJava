package com.tanmeyah.practice.DTO.Responses;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TaskResponseDTO {

    private Long id;
    private String title;
    private String description;
    private boolean completed;

    private Long userId;
    private String userName;
}

