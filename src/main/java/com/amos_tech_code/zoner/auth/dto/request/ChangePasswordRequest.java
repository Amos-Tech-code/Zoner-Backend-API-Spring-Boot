package com.amos_tech_code.zoner.auth.dto.request;

import com.amos_tech_code.zoner.common.validation.StrongPassword;
import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequest(

        @NotBlank(message = "Current password is required")
        String currentPassword,

        @StrongPassword
        String newPassword

) {}