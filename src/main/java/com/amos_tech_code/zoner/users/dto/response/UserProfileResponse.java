package com.amos_tech_code.zoner.users.dto.response;

import com.amos_tech_code.zoner.users.enums.RegistrationStage;
import com.amos_tech_code.zoner.users.enums.Role;

import java.util.UUID;

public record UserProfileResponse(

        UUID id,

        String displayName,

        String username,

        String bio,

        String profilePictureUrl,

        String phone,

        boolean emailVerified,

        Role role,

        RegistrationStage registrationStage

) {}
