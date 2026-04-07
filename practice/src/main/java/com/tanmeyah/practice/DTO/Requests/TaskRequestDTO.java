package com.tanmeyah.practice.DTO.Requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TaskRequestDTO {
    @NotBlank
    @Size(max = 200)
    private String titleEn;

    @Size(max = 200)
    private String titleAr;

    @NotBlank
    @Size(max = 2000)
    private String descriptionEn;

    @Size(max = 2000)
    private String descriptionAr;

    private Boolean completed;
}

