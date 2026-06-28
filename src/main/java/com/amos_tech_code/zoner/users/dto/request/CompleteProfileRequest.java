package com.amos_tech_code.zoner.users.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CompleteProfileRequest(

        @NotBlank
        @Size(max = 100)
        String displayName,

        @NotBlank
        @Size(min = 3, max = 30)
        String username,

        @Size(max = 500)
        String bio,

        String website,

        String phone

) {}