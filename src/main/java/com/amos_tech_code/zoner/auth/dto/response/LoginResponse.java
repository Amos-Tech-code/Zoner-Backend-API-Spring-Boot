package com.amos_tech_code.zoner.auth.dto.response;

import com.amos_tech_code.zoner.users.enums.RegistrationStage;
import com.amos_tech_code.zoner.users.enums.Role;

public record LoginResponse(

        String accessToken,

        String refreshToken,

        RegistrationStage registrationStage,

        Role role,

        boolean emailVerified,

        boolean profileCompleted,

        boolean businessProfileExists

) {}
