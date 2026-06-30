package com.amos_tech_code.zoner.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CompleteProfileRequest(

        @NotBlank(message = "Email is required.")
        String email,

        @NotBlank(message = "First name is required.")
        @Size(min = 2, max = 100, message = "First name must be between 2 and 100 characters.")
        String displayName,

        @NotBlank
        @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters.")
        @Pattern(
                regexp = "^[a-zA-Z0-9._]+$",
                message = "Username may only contain letters, numbers, dots and underscores."
        )
        String username

) {
}
