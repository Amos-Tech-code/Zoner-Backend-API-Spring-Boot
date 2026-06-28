package com.amos_tech_code.zoner.auth.dto.response;

import com.amos_tech_code.zoner.users.enums.RegistrationStage;

import java.util.UUID;

public record RegisterResponse(

        UUID userId,

        String email,

        RegistrationStage registrationStage,

        String message

) {}