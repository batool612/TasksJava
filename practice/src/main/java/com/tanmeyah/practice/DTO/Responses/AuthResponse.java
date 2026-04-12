package com.tanmeyah.practice.DTO.Responses;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    /** Same as JWT {@code role} claim: ROLE_USER or ROLE_ADMIN */
    private String role;
    /** Readable account kind for clients: {@code user} or {@code admin} */
    private String accountType;
}

