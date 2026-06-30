package com.amos_tech_code.zoner.auth.dto.response;

import com.amos_tech_code.zoner.users.enums.RegistrationStage;

import java.util.List;
import java.util.UUID;

public record CompleteProfileResponse(

        UUID userId,

        String displayName,

        String username,

        RegistrationStage registrationStage,

        boolean profileCompleted,

        List<String> usernameSuggestions,

        String message

) {
}
