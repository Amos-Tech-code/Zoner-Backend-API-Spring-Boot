package com.amos_tech_code.zoner.auth.dto.request;

import com.amos_tech_code.zoner.common.validation.StrongPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequest(

        @Email(message = "Invalid email format")
        @NotBlank(message = "Email is required")
        String email,

        @NotBlank(message = "Verification code is required")
        String code,

        @StrongPassword
        String newPassword

) {}
