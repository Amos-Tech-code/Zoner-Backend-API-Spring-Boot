package com.amos_tech_code.zoner.auth.mapper;

import com.amos_tech_code.zoner.auth.dto.response.CompleteProfileResponse;
import com.amos_tech_code.zoner.auth.dto.response.RegisterResponse;
import com.amos_tech_code.zoner.auth.dto.response.VerifyEmailResponse;
import com.amos_tech_code.zoner.users.entity.User;

import java.util.List;

public final class AuthMapper {

    private AuthMapper() {
    }

    public static RegisterResponse toRegisterResponse(
            User user,
            String message
    ) {

        return new RegisterResponse(
                user.getId(),
                user.getEmail(),
                user.getRegistrationStage(),
                message
        );

    }

    public static VerifyEmailResponse toVerifyEmailResponse(
            User user,
            String message
    ) {

        return new VerifyEmailResponse(
                user.getId(),
                user.getRegistrationStage(),
                message
        );

    }

    public static CompleteProfileResponse toCompleteProfileResponse(
            User user,
            String message
    ) {

        return new CompleteProfileResponse(
                user.getId(),
                user.getDisplayName(),
                user.getUsername(),
                user.getRegistrationStage(),
                true,
                List.of(),
                message
        );
    }

    public static CompleteProfileResponse usernameUnavailable(
            User user,
            String requestedUsername,
            List<String> suggestions
    ) {

        return new CompleteProfileResponse(
                user.getId(),
                user.getDisplayName(),
                requestedUsername,
                user.getRegistrationStage(),
                false,
                suggestions,
                "Username is already taken."
        );

    }

}
