package com.amos_tech_code.zoner.auth.mapper;

import com.amos_tech_code.zoner.auth.dto.response.*;
import com.amos_tech_code.zoner.auth.entity.RefreshToken;
import com.amos_tech_code.zoner.users.entity.User;

import java.util.List;
import java.util.UUID;

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

    public static UserResponse toUserResponse(User user) {

        String profilePictureUrl = null;
        if (user.getProfilePicture() != null) {
            profilePictureUrl = user.getProfilePicture().getSecureUrl();
        }

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getDisplayName(),
                profilePictureUrl,
                user.getRole(),
                user.getRegistrationStage(),
                user.isEmailVerified()
        );
    }

    public static LoginResponse toLoginResponse(
            String accessToken,
            String refreshToken,
            Long accessTokenExpiresIn,
            Long refreshTokenExpiresIn,
            User user
    ) {
        return new LoginResponse(
                accessToken,
                refreshToken,
                accessTokenExpiresIn,
                refreshTokenExpiresIn,
                toUserResponse(user)
        );
    }

    public static SessionResponse toSessionResponse(
            RefreshToken token,
            UUID currentSessionId
    ) {

        return new SessionResponse(

                token.getId(),

                token.getDeviceName(),

                token.getDeviceId(),

                token.getPlatform(),

                token.getIpAddress(),

                token.getUserAgent(),

                token.getCreatedAt(),

                token.getLastUsedAt(),

                token.getExpiresAt(),

                token.getId().equals(currentSessionId)
        );

    }
}
