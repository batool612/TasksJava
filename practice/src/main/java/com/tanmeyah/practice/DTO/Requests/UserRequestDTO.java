package com.tanmeyah.practice.DTO.Requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.tanmeyah.practice.validation.NoSpecialCharacters;
import lombok.Data; //getters ,setters

@Data
public class UserRequestDTO {
    @NotBlank
    @Size(min = 2, max = 100)
    @NoSpecialCharacters
    private String name;

    @NotBlank
    @Email
    private String email;
}

