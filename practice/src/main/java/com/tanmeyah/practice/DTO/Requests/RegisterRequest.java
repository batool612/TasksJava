package com.tanmeyah.practice.DTO.Requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.tanmeyah.practice.validation.NoSpecialCharacters;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank
    @Size(min = 2, max = 100)
    @NoSpecialCharacters
    private String name;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 8, max = 72)
    private String password;

    /**
     * Optional. Defaults to USER when omitted.
     * ADMIN is only allowed when {@code app.admin-register-secret} is set and the request sends matching {@code X-Admin-Register-Secret}.
     */
    private RegistrationRole role;
}

