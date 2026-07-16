package com.amos_tech_code.zoner.auth.dto.response;

import com.amos_tech_code.zoner.users.enums.RegistrationStage;
import com.amos_tech_code.zoner.users.enums.Role;

import java.util.UUID;

public record UserResponse(

        UUID id,

        String email,

        String username,

        String displayName,

        String profilePictureUrl,

        Role role,

        RegistrationStage registrationStage,

        boolean emailVerified
) {
}
