package com.amos_tech_code.zoner.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordRequest(
        @Email(message = "Invalid email format")
        @NotBlank(message = "Email is required")
        String email
) {
}

