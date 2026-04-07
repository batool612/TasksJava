package com.tanmeyah.practice.DTO.Responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL) // 3ashan el ar w elm en mesh by display el null values
@Data
@AllArgsConstructor
public class TaskResponseDTO {

    private Long id;
    private String titleEn;
    private String titleAr;
    private String descriptionEn;
    private String descriptionAr;

    private Boolean completed;

    private Long userId;
    private String userName;
}

