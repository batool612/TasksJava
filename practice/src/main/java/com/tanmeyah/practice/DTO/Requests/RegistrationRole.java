package com.tanmeyah.practice.DTO.Requests;

/**
 * Request body value for {@link RegisterRequest#getRole()}.
 * Omit or use USER for normal sign-up. ADMIN requires {@code X-Admin-Register-Secret} when {@code app.admin-register-secret} is set.
 */
public enum RegistrationRole {
    USER,
    ADMIN
}
